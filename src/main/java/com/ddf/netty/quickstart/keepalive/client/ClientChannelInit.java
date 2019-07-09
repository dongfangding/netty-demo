package com.ddf.netty.quickstart.keepalive.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * TCP服务端Channel初始化
 * @author dongfang.ding
 * @date 2019/7/5 10:49
 */
public class ClientChannelInit extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (pipeline != null) {
            pipeline.addLast(new LineBasedFrameDecoder(1024), new StringEncoder(), new StringDecoder())
                    .addLast(new ClientInboundHandler())
                    .addLast(new ClientOutboundHandler());
        }
    }
}
