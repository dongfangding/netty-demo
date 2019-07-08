package com.ddf.netty.quickstart.keepalive.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author dongfang.ding
 * @date 2019/7/5 11:03
 */
public class ClientInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("连接到服务器成功.........");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("与服务器连接断开");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        System.out.println("读取到服务器的发送信息: " + msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
