package com.ddf.netty.quickstart.http.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * http客户端channel初始化
 * @author dongfang.ding
 * @date 2019/7/5 10:49
 */
public class ClientChannelInit extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (pipeline != null) {
            pipeline.addLast(new HttpClientCodec(), new ClientInboundHandler());
            // HTTP消息聚合，将httprequest和httpmessage聚合成对象FullHttpRequest或FullHttpResponse, 使用最大消息值是 512kb
            pipeline.addLast(new HttpObjectAggregator(512 * 1024));
            // 解压
            pipeline.addLast(new HttpContentDecompressor())
                    .addLast(new ClientOutboundHandler());
        }
    }
}
