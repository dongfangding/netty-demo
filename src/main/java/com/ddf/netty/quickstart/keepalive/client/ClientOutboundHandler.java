package com.ddf.netty.quickstart.keepalive.client;

import com.ddf.netty.quickstart.keepalive.server.RequestContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author dongfang.ding
 * @date 2019/7/5 17:38
 */
public class ClientOutboundHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        try {
            System.out.println("向服务端发送数据: " + RequestContent.serial((RequestContent) msg));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ctx.writeAndFlush((msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
