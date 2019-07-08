package com.ddf.netty.quickstart.keepalive.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * 编解码器
 * @author dongfang.ding
 * @date 2019/7/5 15:01
 */
public class RequestContentCodec extends ByteToMessageCodec<Object> {

    /**
     * 服务端操作数据使用对象{@link RequestContent}，最终写入到客户端的时候编码成字节
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof RequestContent) {
            out.writeBytes(new ObjectMapper().writeValueAsBytes(msg));
        }
    }


    /**
     * 将客户端传入的解码成服务端使用的{@link RequestContent}
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // FIXME 如何避免黏包的问题？
        ObjectMapper objectMapper = new ObjectMapper();
        int length = in.readableBytes();
        byte[] array = new byte[length];
        in.readBytes(array);
        out.add(objectMapper.readValue(array, RequestContent.class));
    }
}
