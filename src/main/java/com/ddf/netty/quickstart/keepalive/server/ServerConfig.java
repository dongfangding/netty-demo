package com.ddf.netty.quickstart.keepalive.server;

import io.netty.channel.EventLoopGroup;

/**
 * @author dongfang.ding
 * @date 2019/7/5 16:30
 */
public class ServerConfig {

    /**
     * 连接相关
     */
    private int port;
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private boolean sync;
    private boolean startSsl;

    /**
     * 管理相关
     */
    /**
     * 读超时秒
     */
    private long readerIdleTimeSecond = 30;

    /**
     * 解码时一个完整数据帧的最大长度,超过会无法解码
     */
    private long decodedFrameMaxLength = 1024;


}
