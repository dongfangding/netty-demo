package com.ddf.netty.quickstart.http.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;

/**
 * HTTP服务端Channel初始化
 *
 * @author dongfang.ding
 * @date 2019/7/8 16:54
 */
public class ServerChannelInit extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        // HTTP消息聚合，将httprequest和httpmessage聚合成对象FullHttpRequest或FullHttpResponse, 使用最大消息值是 512kb
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
        // HttpContentCompressor 用于压缩来自 client 支持的 HttpContentCompressor
        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new HttpServerExpectContinueHandler());
        pipeline.addLast(new ServerInboundHandler()).addLast(new ServerOutboundHandler());
    }
}
