package com.ddf.netty.quickstart.http.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author dongfang.ding
 * @date 2019/7/5 14:59
 */
public class RequestContent {
    /**
     * 1 请求 2 应答
     */
    private String type;
    /**
     * 本次请求要做什么事情
     */
    private String cmd;
    /**
     * 主体数据
     */
    private String content;

    public RequestContent() {

    }

    public RequestContent(Type type, Cmd cmd, String content) {
        this.type = type.name();
        this.cmd = cmd.name();
        this.content = content;
    }

    /**
     * 服务端主动向客户端发送数据构造类对象
     *
     * @param content
     * @return
     */
    public static RequestContent rqeuest(String content) {
        return new RequestContent(Type.REQUEST, Cmd.ECHO, content);
    }

    /**
     * 服务端应答客户端
     *
     * @param content
     * @return
     */
    public static RequestContent response(String content) {
        return new RequestContent(Type.RESPONSE, Cmd.ECHO, content);
    }

    /**
     * 服务端向客户端发送心跳检测命令
     *
     * @return
     */
    public static RequestContent heart() {
        return new RequestContent(Type.REQUEST, Cmd.ECHO, "ping");
    }

    /**
     * 序列化RequestContent
     *
     * @param requestContent
     * @return
     * @throws JsonProcessingException
     */
    public static String serial(RequestContent requestContent) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(requestContent);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    /**
     * 连接请求类型
     */
    public enum Type {
        /** 请求 */
        REQUEST,
        /** 应答 */
        RESPONSE
    }

    /**
     * 命名
     */
    public enum Cmd {
        /** 心跳检测 */
        HEART,
        /** 应答服务器 */
        ECHO
    }
}
