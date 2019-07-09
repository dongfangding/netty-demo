package com.ddf.netty.quickstart.keepalive.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * http协议服务端
 *
 * @author dongfang.ding
 * @date 2019/7/5 10:19
 */
public class TCPServer {

    private int port;
    private EventLoopGroup boss;
    private EventLoopGroup worker;

    public TCPServer(int port) {
        this.port = port;
    }


    /**
     * 启动服务端
     */
    public void start() {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ServerChannelInit());
        ChannelFuture future;
        try {
            System.out.println("服务端启动中.....");
            future = serverBootstrap.bind(port).sync();
            if (future.isSuccess()) {
                System.out.println("服务端启动成功....");
            }
            Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new ChannelStoreSyncTask(), 10, 10, TimeUnit.SECONDS);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭服务端
     */
    public void close() {
        try {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new TCPServer(8089).start();
    }
}
