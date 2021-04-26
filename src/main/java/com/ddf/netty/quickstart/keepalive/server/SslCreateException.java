package com.ddf.netty.quickstart.keepalive.server;

/**
 * <p>服务端创建ssl context异常</p >
 *
 * @author Snowball
 * @version 1.0
 * @date 2021/04/26 10:19
 */
public class SslCreateException extends RuntimeException {

    public SslCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
