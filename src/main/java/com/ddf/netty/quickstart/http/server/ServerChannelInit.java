package com.ddf.netty.quickstart.http.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

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
        pipeline.addLast("codec", new HttpServerCodec());
        System.out.println("==============================");
        // HTTP消息聚合, 添加 HttpObjectAggregator 到 ChannelPipeline, 使用最大消息值是 512kb
        pipeline.addLast("aggegator", new HttpObjectAggregator(512 * 1024));
        //  HttpContentCompressor 用于压缩来自 client 支持的 HttpContentCompressor
        pipeline.addLast("compressor",new HttpContentCompressor());
        pipeline.addLast(new ServerInboundHandler()).addLast(new ServerOutboundHandler());
    }
}
