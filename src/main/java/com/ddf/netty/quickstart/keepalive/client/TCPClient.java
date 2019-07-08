package com.ddf.netty.quickstart.keepalive.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
public class TCPClient {

    private String host;
    private int port;
    private volatile Channel channel;
    private ExecutorService executorService;
    private volatile NioEventLoopGroup worker;

    public TCPClient(String host, int port, ExecutorService executorService) {
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
        } finally {
            try {
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        /*for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                TCPClient client = new TCPClient("localhost", 8089, executorService);
                client.connect();
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, String> contentMap = new HashMap<>();
                contentMap.put("deviceId", "HUAWEI-MATE9");
                contentMap.put("from", "13185679963");
                contentMap.put("to", "15564325896");
                contentMap.put("timestamp", System.currentTimeMillis() + "");
                contentMap.put("content", "晚上来家吃饭");
                try {
                    client.write(objectMapper.writeValueAsString(RequestContent.request(objectMapper.writeValueAsString(contentMap))));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                client.close();
            });
        }*/
        TCPClient client = new TCPClient("localhost", 8089, executorService);
        client.connect();
        ObjectMapper objectMapper = new ObjectMapper();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("requestId", UUID.randomUUID().toString());
            requestMap.put("type", "REQUEST");
            requestMap.put("cmd", "ECHO");
            Map<String, String> contentMap = new HashMap<>();
            contentMap.put("deviceId", "HUAWEI-MATE9");
            contentMap.put("from", "13185679963");
            contentMap.put("to", "15564325896");
            contentMap.put("timestamp", System.currentTimeMillis() + "");
            contentMap.put("content", "晚上来家吃饭晚上来家吃饭晚上来家吃饭晚");
            requestMap.put("body", objectMapper.writeValueAsString(contentMap));
            client.write(objectMapper.writeValueAsString(requestMap) + "\r\n");
        }
    }
}
