package com.ddf.netty.quickstart.keepalive.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dongfang.ding
 * @date 2019/7/5 15:52
 */
@ChannelHandler.Sharable
public class ServerInboundHandler extends SimpleChannelInboundHandler<RequestContent> {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static Map<String, ChannelInfo> channelStore = new ConcurrentHashMap<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channels.add(ctx.channel());
        channelStore.put(ctx.channel().remoteAddress().toString(), ChannelInfo.registry(ctx.channel()));
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]注册成功.........");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]在线.........");
        ChannelInfo.active(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]掉线");
        ChannelInfo.inactive(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestContent msg) throws JsonProcessingException {
        putMessage(ctx.channel(), msg);
        System.out.println("接收到客户端[" + ctx.channel().remoteAddress() + "]发送的数据: " + RequestContent.serial(msg));
        ctx.writeAndFlush(RequestContent.response(msg.getContent()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 将消息放入对应的客户端的消息队列中
     *
     * @param channel
     * @param requestContent
     */
    private void putMessage(Channel channel, RequestContent requestContent) {
        String key = channel.remoteAddress().toString();
        ChannelInfo channelInfo = ServerInboundHandler.channelStore.get(key);
        // 可能永远也不会出现这种情况
        if (channelInfo == null) {
            channelInfo = ChannelInfo.active(channel);
            channelInfo.getQueue().add(requestContent);
            ServerInboundHandler.channelStore.put(key, channelInfo);
        }
        channelInfo.getQueue().add(requestContent);
    }
}
