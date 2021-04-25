package com.ddf.netty.quickstart.keepalive.client;

import com.ddf.netty.quickstart.keepalive.server.KeyManagerFactoryHelper;
import com.ddf.netty.quickstart.keepalive.server.RequestContent;
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
 * @author dongfang.ding
 * @date 2019/7/5 11:12
 */
public class TCPClient {

    private String host;
    private int port;
    private volatile Channel channel;
    private ExecutorService executorService;
    private NioEventLoopGroup worker;
    private boolean startSsl;
    /** 重连策略 */
    private RetryPolicy retryPolicy;;

    public TCPClient(String host, int port, ExecutorService executorService, boolean startSsl, RetryPolicy retryPolicy) {
        this.host = host;
        this.port = port;
        this.executorService = executorService;
        this.startSsl = startSsl;
        this.retryPolicy = retryPolicy;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public void connect() {
//        executorService.execute(() -> {
            worker = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker).channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
            try {
                if (startSsl) {
                    bootstrap.handler(new ClientChannelInit(this, KeyManagerFactoryHelper.defaultClientContext()));
                } else {
                    bootstrap.handler(new ClientChannelInit());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            bootstrap.connect(host, port);

//            ChannelFuture future;
//            try {
//                future = bootstrap.connect().sync();
//                if (future.isSuccess()) {
//                    System.out.println("连接到服务端端成功....");
//                }
//                channel = future.channel();
//                System.out.println("客户端初始化完成............");
//                // 这里会一直与服务端保持连接，直到服务端断掉才会同步关闭自己,所以是阻塞状态，如果不实用线程的话，无法将对象暴露出去给外部调用
//                channel.closeFuture().sync();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            ChannelFuture future = bootstrap.connect(host, port);
            future.addListener(getConnectionListener());
            this.channel = future.channel();
//        });
    }

    private ChannelFutureListener getConnectionListener() {
        return future -> {
            if (!future.isSuccess()) {
                future.channel().pipeline().fireChannelInactive();
            }
        };
    }

    public void write(RequestContent content) {
        while (channel == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        channel.writeAndFlush((content));
    }

    public void close() {
        try {
            System.out.println("客户端尝试主动close..............");
            channel.close();
        } finally {
            try {
                worker.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws JsonProcessingException, InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        ObjectMapper objectMapper = new ObjectMapper();

        final ExponentialBackOffRetry backOffRetry = new ExponentialBackOffRetry(1000, Integer.MAX_VALUE, 60 * 1000);

        TCPClient client = new TCPClient("localhost", 8089, executorService, true, backOffRetry);
        client.connect();
//
        Thread.sleep(5000);
        client.close();

//        while (true) {
//            Thread.sleep(2000);
//            Map<String, String> contentMap = new HashMap<>();
//            contentMap.put("from", "13185679963");
//            contentMap.put("to", "15564325896");
//            contentMap.put("timestamp", System.currentTimeMillis() + "");
//            contentMap.put("content", "晚上来家吃饭晚上来家吃饭晚上来家吃饭晚");
//            RequestContent request = RequestContent.request(objectMapper.writeValueAsString(contentMap));
//            // 以append的方式增加扩展字段
//            request.addExtra("lang", "java");
//            request.addExtra("devieId", "huawei");
//            // 写json串
//            client.write(request);
//        }
    }
}
