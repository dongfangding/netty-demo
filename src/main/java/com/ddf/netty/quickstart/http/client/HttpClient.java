package com.ddf.netty.quickstart.http.client;

import com.ddf.netty.quickstart.http.server.RequestContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * {
 *  "type": 1 (心跳包) 2 (内容)
 *  "content": "消息内容"
 * }
 *
 *
 *
 *
 *
 *
 *
 *
 * @author dongfang.ding
 * @date 2019/7/5 11:12
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
                System.out.println("客户端初始化完成............");
                // 这里会一直与服务端保持连接，直到服务端断掉才会同步关闭自己,所以是阻塞状态，如果不实用线程的话，无法将对象暴露出去给外部调用
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public void write(String content) {
        while (channel == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        channel.writeAndFlush(content);
    }

    public void write(byte[] content) {
        while (channel == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        channel.writeAndFlush(content);
    }

    public void close() {
        try {
            System.out.println("客户端尝试主动close..............");
            channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
            executorService.shutdown();
        } finally {
            try {
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        HttpClient client = new HttpClient("localhost", 8089, executorService);
        client.connect();
        ObjectMapper objectMapper = new ObjectMapper();
        client.write(objectMapper.writeValueAsString(RequestContent.rqeuest("我是一个粉刷匠")));
        Thread.sleep(10000);
        client.close();
    }
}
