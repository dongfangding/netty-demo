package com.ddf.netty.quickstart.keepalive.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

/**
 * @author dongfang.ding
 * @date 2019/7/5 14:59
 */
public class RequestContent {

    /**
     * 唯一标识次数请求
     */
    private String requestId;
    /**
     * 1 请求 2 应答
     */
    private String type;
    /**
     * 本次请求要做什么事情
     */
    private String cmd;

    /**
     * 请求时间
     */
    @JsonIgnore
    private Long requestTime;

    /**
     * 响应时间
     */
    @JsonIgnore
    private Long responseTime;
    /**
     * 主体数据
     */
    private String body;

    public RequestContent() {

    }

    public RequestContent(String requestId, Type type, Cmd cmd, Long requestTime, String content) {
        this.requestId = requestId;
        this.type = type.name();
        this.cmd = cmd.name();
        this.requestTime = requestTime;
        this.body = content;
    }

    /**
     * 服务端主动向客户端发送数据构造类对象
     *
     * @param content
     * @return
     */
    public static RequestContent request(String content) {
        return new RequestContent(UUID.randomUUID().toString(), Type.REQUEST, Cmd.ECHO, System.currentTimeMillis(), content);
    }

    /**
     * 服务端应答客户端数据已收到
     *
     * @param requestContent
     * @return
     */
    public static RequestContent responseAccept(RequestContent requestContent) {
        return response(requestContent, "202");
    }

    /**
     * 服务端应答客户端处理成功
     *
     * @param requestContent
     * @return
     */
    public static RequestContent responseOK(RequestContent requestContent) {
        return response(requestContent, "200");
    }

    private static RequestContent response(RequestContent requestContent, String code) {
        RequestContent response = new RequestContent();
        response.setType(Type.RESPONSE.name());
        response.setRequestId(requestContent.getRequestId());
        response.setCmd(requestContent.getCmd());
        response.setResponseTime(System.currentTimeMillis());
        response.setBody(code);
        return response;
    }

    /**
     * 服务端向客户端发送心跳检测命令
     *
     * @return
     */
    public static RequestContent heart() {
        return new RequestContent(UUID.randomUUID().toString(), Type.REQUEST, Cmd.ECHO, System.currentTimeMillis(), "ping");
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(long requestTime) {
        this.requestTime = requestTime;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
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
