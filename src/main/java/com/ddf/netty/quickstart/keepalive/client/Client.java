package com.ddf.netty.quickstart.keepalive.client;

import com.ddf.netty.quickstart.keepalive.server.MessageDecoder;
import com.ddf.netty.quickstart.keepalive.server.MessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Client {
    private int port;
    private String host;
    private volatile SocketChannel socketChannel;

    private static Map<Channel, ChannelHandlerContext> contextMap = new ConcurrentHashMap<>();

    public Client(int port, String host) {
        this.host = host;
        this.port = port;
        start();
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public static Map<Channel, ChannelHandlerContext> getContextMap() {
        return contextMap;
    }

    private void start() {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                // 保持连接
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 有数据立即发送
                .option(ChannelOption.TCP_NODELAY, true)
                // 绑定处理group
                .group(eventLoopGroup).remoteAddress(host, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 初始化编码器，解码器，处理器
                        socketChannel.pipeline().addLast(
                                new MessageDecoder(),
                                new MessageEncoder(),
                                new ClientHandler());
                    }
                });
        // 进行连接
        ChannelFuture future;
        try {
            future = bootstrap.connect(host, port).sync();
            // 判断是否连接成功
            if (future.isSuccess()) {
                // 得到管道，便于通信
                socketChannel = (SocketChannel) future.channel();
                System.out.println("客户端开启成功...");
            } else {
                System.out.println("客户端开启失败...");
            }
            System.out.println("====================1======================");
            // 等待客户端链路关闭
            socketChannel.closeFuture().sync();
            System.out.println("====================2======================");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅地退出，释放相关资源
            System.out.println("=================退出=============================");
            eventLoopGroup.shutdownGracefully();
            System.out.println("=================退出成功=============================");
        }
    }

    public void sendMessage(Object msg) {
        // 因为连接是异步的，可能会存在调用了发消息但是连接还没成功，所以要在这里等待
        System.out.println(socketChannel);
        while (socketChannel == null) {
            System.out.println("||||||||||||||||||");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("出来吧。。。。");
        socketChannel.writeAndFlush(msg);
    }

    public void close(Channel channel) {
        ChannelHandlerContext channelHandlerContext = contextMap.get(channel);
        channelHandlerContext.close();
        System.out.println("关闭连接： " + channel);
        contextMap.remove(channel);
    }
}