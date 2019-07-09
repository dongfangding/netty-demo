package com.ddf.netty.quickstart.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP客户端
 *
 * @author dongfang.ding
 * @date 2019/7/8 17:01
 */
public class HttpClient {

    private String host;
    private int port;
    private volatile Channel channel;
    private ExecutorService executorService;
    private volatile NioEventLoopGroup worker;

    public HttpClient(String host, int port, ExecutorService executorService) {
        this.host = host;
        this.port = port;
        this.executorService = executorService;
    }

    public void connect() {
        executorService.execute(() -> {
            worker = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .remoteAddress(host, port)
                    .handler(new ClientChannelInit());
            ChannelFuture future;
            try {
                future = bootstrap.connect().sync();
                if (future.isSuccess()) {
                    System.out.println("连接到服务端端成功....");
                }
                channel = future.channel();
                channel.writeAndFlush("haha");
                System.out.println("客户端初始化完成............");
                // 这里会一直与服务端保持连接，直到服务端断掉才会同步关闭自己,所以是阻塞状态，如果不实用线程的话，无法将对象暴露出去给外部调用
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void post(String content) {
        while (channel == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "hehe",
                Unpooled.copiedBuffer(content.getBytes(CharsetUtil.UTF_8)));
        HttpUtil.setContentLength(request, request.content().readableBytes());
        HttpUtil.setKeepAlive(request, true);
        request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
        channel.writeAndFlush(request);
    }

    public void close() {
        try {
            System.out.println("客户端尝试主动close..............");
            channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
        } finally {
            try {
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        HttpClient client = new HttpClient("localhost", 8088, executorService);
        client.connect();
        client.post("{\"id\": 222}");
    }
}
