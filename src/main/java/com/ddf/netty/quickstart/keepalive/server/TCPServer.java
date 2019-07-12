package com.ddf.netty.quickstart.keepalive.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
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

    private static int WORKER_GROUP_SIZE = Runtime.getRuntime().availableProcessors() * 2;

    private int port;
    private EventLoopGroup boss;
    private EventLoopGroup worker;
    private boolean sync;
    private boolean startSsl;

    public TCPServer(int port) {
        this.port = port;
        this.sync = true;
        this.startSsl = false;
    }

    public TCPServer(int port, boolean startSsl, boolean sync) {
        this.port = port;
        this.startSsl = startSsl;
        this.sync = sync;
    }


    /**
     * 启动服务端
     */
    public void start() throws Exception {
        boss = new NioEventLoopGroup();
        worker = new NioEventLoopGroup(WORKER_GROUP_SIZE);
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, worker);
        serverBootstrap.channel(NioServerSocketChannel.class);
        System.out.println("workerGroup size:" + WORKER_GROUP_SIZE);
        serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(false))
                .childOption(ChannelOption.SO_RCVBUF, 1048576)
                .childOption(ChannelOption.SO_SNDBUF, 1048576);
        try {
            if (startSsl) {
                serverBootstrap.childHandler(new ServerChannelInit(KeyManagerFactoryHelper.defaultServerContext()));
            } else {
                serverBootstrap.childHandler(new ServerChannelInit());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        ChannelFuture future;
        try {
            System.out.println("服务端启动中.....");
            future = serverBootstrap.bind(port).sync();
            if (future.isSuccess()) {
                System.out.println("服务端启动成功....");
            }
            if (sync) {
                Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new ChannelStoreSyncTask(), 10, 10, TimeUnit.SECONDS);
            }
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
        try {
            new TCPServer(8089, true, true).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
