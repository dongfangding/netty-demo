package com.ddf.netty.quickstart.http.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.CharsetUtil;

/**
 * @author dongfang.ding
 * @date 2019/7/5 15:52
 */
@ChannelHandler.Sharable
public class ServerInboundHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]注册成功.........");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]在线.........");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("客户端[" + ctx.channel().remoteAddress() + "]掉线");
    }

    /**
     * 如果是聊天室的功能，其实就是服务端收到消息之后，然后再由服务端向所有连接的客户端转发这个消息而已
     *
     * @param ctx
     * @param request
     * @throws JsonProcessingException
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object request) {
        if (request instanceof FullHttpRequest) {
            FullHttpRequest httpRequest = (FullHttpRequest) request;
            // 获取请求连接地址
            String uri = httpRequest.uri();
            HttpMethod method = httpRequest.method();
            // 获取请求头
            HttpHeaders headers = httpRequest.headers();
            // 获取请求体
            String content = httpRequest.content().toString(CharsetUtil.UTF_8);
            System.out.println(uri + "==>" + method + "==>" + content);
            Util.response(ctx, httpRequest, content);
        }
    }


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
