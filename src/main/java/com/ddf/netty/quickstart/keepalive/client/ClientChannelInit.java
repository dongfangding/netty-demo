package com.ddf.netty.quickstart.keepalive.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author dongfang.ding
 * @date 2019/7/5 10:49
 */
public class ClientChannelInit extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (pipeline != null) {
            pipeline.addLast(new StringEncoder(), new StringDecoder(), new ClientInboundHandler())
                    .addLast(new ClientOutboundHandler());
        }
    }
}
