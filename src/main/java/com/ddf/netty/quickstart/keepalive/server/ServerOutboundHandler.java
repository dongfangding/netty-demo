package com.ddf.netty.quickstart.keepalive.server;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author dongfang.ding
 * @date 2019/7/5 15:16
 */
public class ServerOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws JsonProcessingException {
        System.out.println("向客户端[" + ctx.channel().remoteAddress() + "]发送数据: " + RequestContent.serial((RequestContent) msg));
        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
