package com.ddf.netty.quickstart.keepalive.server;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * 生成服务器SslContext
 *
 * 1. 生成服务端证书,证书密码是123456
 * keytool -genkey -alias securechat -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass server_123456 -storepass server_123456 -keystore server.jks
 *
 * 2. 将生成的文件导入到证书中存储
 *  keytool -export -alias securechat -keystore server.jks -storepass server_123456 -file server.cer
 *
 * 3. 生成客户端
 * keytool -genkey -alias smcc -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass client_123456 -storepass client_123456 -keystore client.jks
 *
 * 4. 将客户端证书导入到服务端的秘钥库中，并且授信
 * keytool -import -trustcacerts -alias securechat -file server.cer -storepass client_123456 -keystore client.jks
 *
 *
 * @author dongfang.ding
 * @date 2019/7/12 15:13
 */
public class KeyManagerFactoryHelper {

    private static KeyStore keyStore;
    private static KeyManagerFactory keyManagerFactory;
    private static TrustManagerFactory trustManagerFactory;
    private static final String DEFAULT_SERVER_PATH = System.getProperty("user.dir") + "/src/main/resources/cer/server.jks";
    private static final String DEFAULT_CLIENT_PATH = System.getProperty("user.dir") + "/src/main/resources/cer/client.jks";
    private static final String DEFAULT_SERVER_PASS = "server_123456";
    private static final String DEFAULT_CLIENT_PASS = "client_123456";

    static {
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        } catch (KeyStoreException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建默认的服务端SslContext
     * @return
     * @throws Exception
     */
    public static SslContext defaultServerContext() throws Exception {
        return KeyManagerFactoryHelper.createServerContext(DEFAULT_SERVER_PATH, DEFAULT_SERVER_PASS);
    }

    /**
     * 创建默认的客户端SslContext
     * @return
     * @throws Exception
     */
    public static SslContext defaultClientContext() throws Exception {
        return KeyManagerFactoryHelper.createServerContext(DEFAULT_CLIENT_PATH, DEFAULT_CLIENT_PASS);
    }

    /**
     * 生成服务端SslContext
     * @param caPath
     * @param caPassword
     * @return
     * @throws Exception
     */
    public static SslContext createServerContext(String caPath, String caPassword) throws Exception {
        keyStore.load(new FileInputStream(caPath), caPassword.toCharArray());
        keyManagerFactory.init(keyStore, caPassword.toCharArray());
        return SslContextBuilder.forServer(keyManagerFactory).build();
    }


    /**
     * 生成客户端SslContext
     * @param caPath
     * @param caPassword
     * @return
     * @throws Exception
     */
    public static SslContext createClientContext(String caPath, String caPassword) throws Exception {
        keyStore.load(new FileInputStream(caPath), caPassword.toCharArray());
        trustManagerFactory.init(keyStore);
        return SslContextBuilder.forClient().trustManager(trustManagerFactory).build();
    }
}
