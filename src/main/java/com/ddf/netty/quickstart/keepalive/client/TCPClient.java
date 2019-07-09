package com.ddf.netty.quickstart.keepalive.client;

import com.ddf.netty.quickstart.keepalive.server.RequestContent;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
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
        channel.writeAndFlush(content + "\r\n");
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
        ExecutorService executorService = Executors.newCachedThreadPool();
        ObjectMapper objectMapper = new ObjectMapper();
        for (int i = 0; i < 10; i++) {
            TCPClient client = new TCPClient("localhost", 8089, executorService);
            client.connect();
            executorService.execute(() -> {
                for (int j = 0; j < 10; j++) {
                    try {
                        // 写json串
                        Map<String, String> contentMap = new HashMap<>();
                        contentMap.put("from", "13185679963");
                        contentMap.put("to", "15564325896");
                        contentMap.put("timestamp", System.currentTimeMillis() + "");
                        contentMap.put("content", "晚上来家吃饭晚上来家吃饭晚上来家吃饭晚");
                        RequestContent request = RequestContent.request(objectMapper.writeValueAsString(contentMap))
                                .setExtra("deviceId: HUAWEI-MATE9;请求头: 请求值");
                        client.write(objectMapper.writeValueAsString(request));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
