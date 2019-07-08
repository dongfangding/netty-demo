package com.ddf.netty.quickstart.http.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
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
     * @param ctx
     * @param request
     * @throws JsonProcessingException
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object request) {
        response(ctx, "哈哈");
        FullHttpRequest httpRequest = (FullHttpRequest) request;
        // 获取请求连接地址
        String uri = httpRequest.uri();
        HttpMethod method = httpRequest.method();
        // 获取请求头
        HttpHeaders headers = httpRequest.headers();
        // 获取请求体
        String content = httpRequest.content().toString(CharsetUtil.UTF_8);
        System.out.println("uri: " + uri + ", method: " + method + ", headers: " + headers.toString() + ", content: " + content);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }


    /**
     * 写入响应体
     * @param ctx
     * @param content
     */
    private void response(ChannelHandlerContext ctx, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(content.getBytes(CharsetUtil.UTF_8)));
        response.headers().add(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
