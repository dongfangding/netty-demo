package com.ddf.netty.quickstart.http.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

/**
 *
 * 工具类
 * @author dongfang.ding
 * @date 2019/7/9 9:46
 */
public class Util {

    private Util() {}


    /**
     * 写入响应体
     * @param ctx
     * @param httpRequest
     * @param content
     */
    public static void response(ChannelHandlerContext ctx, FullHttpRequest httpRequest, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(content.getBytes(CharsetUtil.UTF_8)));
        boolean keepAlive = HttpUtil.isKeepAlive(httpRequest);
        HttpUtil.setKeepAlive(response, keepAlive);
        HttpUtil.setContentLength(response, content.getBytes(StandardCharsets.UTF_8).length);
        String contentType = httpRequest.headers().get("Content-Type");
        if (null == contentType || "".equals(content)) {
            contentType = "application/json;charset=UTF-8";
        }
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        if (keepAlive) {
            ctx.write(response);
        } else {
            // 如果客户端不要求长连接，则返回消息之后关闭连接
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }



    /**
     * 写入响应体
     * @param ctx
     * @param content
     */
    public static void response(ChannelHandlerContext ctx, String content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                Unpooled.copiedBuffer(content.getBytes(CharsetUtil.UTF_8)));
        HttpUtil.setKeepAlive(response, true);
        HttpUtil.setContentLength(response, content.getBytes(StandardCharsets.UTF_8).length);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=UTF-8");
        ctx.write(response);
    }
}


