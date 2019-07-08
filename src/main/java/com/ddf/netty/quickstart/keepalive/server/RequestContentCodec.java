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
     * 注意TCP的粘包和拆包问题，这里已经使用了{@link io.netty.handler.codec.LineBasedFrameDecoder}解码器来解决，要求客户端比如以换行符结尾
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        int length = in.readableBytes();
        byte[] array = new byte[length];
        in.readBytes(array);
        RequestContent requestContent = objectMapper.readValue(array, RequestContent.class);
        // 以客户端为准还是服务端自己设置？
        requestContent.setRequestTime(System.currentTimeMillis());
        out.add(requestContent);
    }
}
