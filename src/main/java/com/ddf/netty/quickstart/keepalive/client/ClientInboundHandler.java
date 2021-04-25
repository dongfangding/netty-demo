package com.ddf.netty.quickstart.keepalive.client;

import com.ddf.netty.quickstart.keepalive.server.RequestContent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author dongfang.ding
 * @date 2019/7/5 11:03
 */
@ChannelHandler.Sharable
public class ClientInboundHandler extends ChannelInboundHandlerAdapter {

    final InternalLogger log = Log4J2LoggerFactory.getInstance(ClientInboundHandler.class);

    private final TCPClient tcpClient;

    public ClientInboundHandler(TCPClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("连接到服务器成功>>>>>>>>>>>>>>>>>.");
        ping(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("与服务器连接断开>>>>>>>>>>>>>>>");
        reconnect(ctx, 10);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("读取到服务器的发送信息>>>>>>>>>>>", msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 发送心跳包
     *
     * @param channel
     */
    @SuppressWarnings("unchecked")
    private void ping(Channel channel) {
        int heartBeatPeriod = 5;
        log.info("=============发送心跳包" + Thread.currentThread().getName());
        ScheduledFuture<?> future = channel.eventLoop()
                .schedule(() -> {
                    if (channel.isActive()) {
                        log.info("sending heart beat to the server...");
                        channel.writeAndFlush(RequestContent.heart());
                    } else {
                        log.info("The connection had broken, cancel the task that will send a heart beat.");
                        channel.closeFuture();
                        throw new RuntimeException();
                    }
                }, heartBeatPeriod, TimeUnit.SECONDS);

        future.addListener((GenericFutureListener) future1 -> {
            if (future1.isSuccess()) {
                ping(channel);
            }
        });
    }

    private void reconnect(ChannelHandlerContext ctx, int retries) {
        if (retries == 0) {
            log.info("连接关闭， 已关闭重连机制， 连接将断开>>>>>>>>>>>>>");
            ctx.close();
        }

        final RetryPolicy retryPolicy = tcpClient.getRetryPolicy();

        boolean allowRetry = retryPolicy.allowRetry(retries);
        if (allowRetry) {
            long sleepTimeMs = retryPolicy.getSleepTimeMs(retries);
            log.info(String.format("Try to reconnect to the server after %dms. Retry count: %d.", sleepTimeMs,
                    ++retries
            ));
            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(tcpClient::connect, sleepTimeMs, TimeUnit.MILLISECONDS);
        }
        ctx.fireChannelInactive();
    }
}
