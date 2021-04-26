package com.ddf.netty.quickstart.keepalive.server;

/**
 * <p>服务端启动异常</p >
 *
 * @author Snowball
 * @version 1.0
 * @date 2021/04/26 10:23
 */
public class ServerStartException extends RuntimeException {

    public ServerStartException(String message, Throwable cause) {
        super(message, cause);
    }
}
