package com.ddf.netty.quickstart.keepalive.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {

    private ServerSocketChannel serverSocketChannel;

    public Server(int serverPort) {
        bind(serverPort);
    }

    private void bind(int serverPort) {
        NettyConfig.executorService.execute(() -> {
            // 服务端要建立两个group，一个负责接收客户端的连接，一个负责处理数据传输
            // 连接处理group
            EventLoopGroup boss = new NioEventLoopGroup();
            // 事件处理group
            EventLoopGroup worker = new NioEventLoopGroup();
            // 启动辅助类
            ServerBootstrap bootstrap = new ServerBootstrap();
            // 绑定处理group
            bootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    // 服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 处理新连接
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            // 增加任务处理
                            ChannelPipeline p = sc.pipeline();
                            p.addLast(
                                    // 使用了netty自带的编码器和解码器
                                    // new StringDecoder(Charset.forName("utf-8")),
                                    // new StringEncoder(Charset.forName("utf-8")),
                                    new MessageDecoder(),
                                    new MessageEncoder(),
                                    //自定义的处理器
                                    new ServerHandler());
                        }
                    });

            // 绑定端口，同步等待成功
            ChannelFuture future;
            try {
                future = bootstrap.bind(serverPort).sync();
                if (future.isSuccess()) {
                    serverSocketChannel = (ServerSocketChannel) future.channel();
                    System.out.println("服务端开启成功");
                } else {
                    System.out.println("服务端开启失败");
                }
                //等待服务监听端口关闭,就是由于这里会将线程阻塞，导致无法发送信息，所以我这里开了线程
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 优雅地退出，释放线程池资源
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        });
    }

    public void sendMessage(Object msg) {
        if (serverSocketChannel != null) {
            serverSocketChannel.writeAndFlush(msg);
        }
    }
}