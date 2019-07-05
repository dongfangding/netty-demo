package com.ddf.netty.quickstart.http.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author dongfang.ding
 * @date 2019/7/5 15:52
 */
public class ServerInboundHandler extends SimpleChannelInboundHandler<RequestContent> {

    /** 一个连接最多阻塞1024个任务 */
    public static BlockingQueue<RequestContent> contentQueue = new ArrayBlockingQueue<>(1024);

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]注册成功.........");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]连接进来.........");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]连接断开");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestContent msg) throws JsonProcessingException {
        System.out.println("接收到客户端[" + ctx.channel().remoteAddress() + "]发送的数据: " + RequestContent.serial(msg));
        ctx.writeAndFlush(RequestContent.response(msg.getContent()));
        contentQueue.add(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
