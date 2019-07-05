package com.ddf.netty.quickstart.keepalive.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

public class MessageEncoder extends MessageToByteEncoder<RequestInfo> {


    @Override
    protected void encode(ChannelHandlerContext ctx, RequestInfo msg, ByteBuf out) throws Exception {

        ByteBufOutputStream writer = new ByteBufOutputStream(out);

        writer.writeByte(msg.getType());
        byte[] info = null;

        if (msg.getInfo() != null && !"".equals(msg.getInfo())) {
            info = msg.getInfo().getBytes(StandardCharsets.UTF_8);
            writer.write(info);
        }
    }
}