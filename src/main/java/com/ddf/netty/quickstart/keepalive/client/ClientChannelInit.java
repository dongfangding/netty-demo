package com.ddf.netty.quickstart.keepalive.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLEngine;

/**
 * TCP服务端Channel初始化
 * @author dongfang.ding
 * @date 2019/7/5 10:49
 */
public class ClientChannelInit extends ChannelInitializer<Channel> {

    private final SslContext context;

    public ClientChannelInit(SslContext context) {
        this.context = context;
    }

    public ClientChannelInit() {
        context = null;
    }

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (pipeline != null) {
            if (context != null) {
                SSLEngine engine = context.newEngine(ch.alloc());
                engine.setUseClientMode(true);
                ch.pipeline().addFirst("ssl", new SslHandler(engine));
            }

            pipeline.addLast(new LineBasedFrameDecoder(1024))
                    // 指定字符串编解码器，客户端直接写入字符串，不需要使用ByteBuf
                    .addLast(new StringEncoder(CharsetUtil.UTF_8))
                    .addLast(new StringDecoder(CharsetUtil.UTF_8))
                    .addLast(new ClientInboundHandler())
                    .addLast(new ClientOutboundHandler());
        }
    }
}
