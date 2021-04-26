package com.ddf.netty.quickstart.keepalive.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLEngine;

/**
 * 服务端的handler
 *
 * @author dongfang.ding
 * @date 2019/7/5 10:49
 */
public class ServerChannelInit extends ChannelInitializer<Channel> {

    private final SslContext context;

    public ServerChannelInit() {
        context = null;
    }

    public ServerChannelInit(SslContext context) {
        this.context = context;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (pipeline != null) {
            if (context != null) {
                SSLEngine engine = context.newEngine(ch.alloc());
                engine.setUseClientMode(false);
                ch.pipeline()
                        .addFirst("ssl", new SslHandler(engine));

            }

            // 添加换行符解码器，以及自定义编解码器,客户端每次传输数据必须以"\r\n"结尾并且符合自定义解码器规则
            // 限制每个完整帧最大的字节长度
            pipeline.addLast(new LineBasedFrameDecoder(1024))
                    .addLast(new RequestContentCodec())
                    // IdleStateHandler 将通过 IdleStateEvent 调用 userEventTriggered。
                    // 注意顺序，只有在这个IdleStateHandler后定义的handler的userEventTriggered才会被循环触发
                    .addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS))
                    .addLast(new ServerOutboundHandler())
                    .addLast(new ServerInboundHandler());
        }
    }
}
