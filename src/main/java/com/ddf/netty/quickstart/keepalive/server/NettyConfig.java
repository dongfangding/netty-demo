package com.ddf.netty.quickstart.keepalive.server;

import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dongfang.ding
 * @date 2019/7/4 10:06
 */
public class NettyConfig {


    /**
     * 存储每一个客户端接入进来时的channel对象
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static ExecutorService executorService = Executors.newCachedThreadPool();
}
