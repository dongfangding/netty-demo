package com.ddf.netty.quickstart.keepalive.server;

import io.netty.channel.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 *
 * 服务端的handler
 *
 * @author dongfang.ding
 * @date 2019/7/5 10:49
 */
public class ServerChannelInit extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (pipeline != null) {
            pipeline.addLast(new RequestContentCodec())
                    .addLast(new ServerOutboundHandler()).addLast(new ServerInboundHandler())
                    // IdleStateHandler 将通过 IdleStateEvent 调用 userEventTriggered ，如果连接没有接收或发送数据超过60秒钟
                    .addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS))
                    .addLast(new HeartbeatHandler());
        }
    }


    /**
     * 心跳检测类
     */
    public static final class HeartbeatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                // 发送的心跳并添加一个侦听器，如果发送操作失败将关闭连接
                ctx.writeAndFlush(RequestContent.heart())
                        .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
                // 事件不是一个 IdleStateEvent 的话，就将它传递给下一个处理程序
                super.userEventTriggered(ctx, evt);
            }
        }
    }
}
