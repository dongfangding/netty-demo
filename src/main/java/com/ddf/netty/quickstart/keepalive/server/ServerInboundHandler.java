package com.ddf.netty.quickstart.keepalive.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
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

    /**
     * 连接注册事件
     *
     * @param ctx
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        channels.add(ctx.channel());
        channelStore.put(ctx.channel().remoteAddress().toString(), ChannelInfo.registry(ctx.channel()));
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]注册成功.........");
        ctx.fireChannelRegistered();
    }

    /**
     * 连接建立事件
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]在线.........");
        ChannelInfo.active(ctx.channel());
        ctx.fireChannelActive();
    }


    /**
     * 连接断开事件
     *
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]掉线");
        ChannelInfo.inactive(ctx.channel());
        channels.remove(ctx.channel());
        ctx.fireChannelInactive();
    }


    /**
     * 如果是聊天室的功能，其实就是服务端收到消息之后，然后再由服务端向所有连接的客户端转发这个消息而已
     *
     * @param ctx
     * @param msg
     * @throws JsonProcessingException
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestContent msg) throws JsonProcessingException {
        putMessage(ctx.channel(), msg);
        System.out.println("接收到客户端[" + ctx.channel().remoteAddress() + "]发送的数据: " + RequestContent.serial(msg));
        ctx.writeAndFlush(RequestContent.responseAccept(msg));
    }


    /**
     * 处理心跳检测
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                System.out.printf("%s未发送心跳数据, 断开连接>>>>>%n", ctx.channel().toString());
                // 在规定时间内没有收到客户端的上行数据, 主动断开连接
                ctx.disconnect();
            }
        } else {
            // 事件不是一个 IdleStateEvent 的话，就将它传递给下一个处理程序
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        System.out.println("客户端[" + ctx.channel() + "]出现异常，关闭连接");
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
