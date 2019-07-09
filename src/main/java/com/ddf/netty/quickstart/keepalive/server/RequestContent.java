package com.ddf.netty.quickstart.keepalive.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author dongfang.ding
 * @date 2019/7/5 14:59
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestContent implements Serializable {

    /**
     * 唯一标识次数请求
     */
    @JsonInclude
    private String requestId;
    /**
     * 1 请求 2 应答
     */
    @JsonInclude
    private String type;
    /**
     * 本次请求要做什么事情
     */
    @JsonInclude
    private String cmd;

    /**
     * 请求时间
     */
    private Long requestTime;

    /**
     * 响应时间
     */
    private Long responseTime;
    /**
     * 主体数据
     */
    private String body;

    /**
     * 扩展字段
     * 类似http请求头，解析格式为key1: value1; key2: value2
     */
    private String extra;

    @JsonIgnore
    private transient Map<String, String> extraMap;

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

    public String getRequestId() {
        return requestId;
    }

    public RequestContent setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getType() {
        return type;
    }

    public RequestContent setType(String type) {
        this.type = type;
        return this;
    }

    public String getCmd() {
        return cmd;
    }

    public RequestContent setCmd(String cmd) {
        this.cmd = cmd;
        return this;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public RequestContent setRequestTime(Long requestTime) {
        this.requestTime = requestTime;
        return this;
    }

    public Long getResponseTime() {
        return responseTime;
    }

    public RequestContent setResponseTime(Long responseTime) {
        this.responseTime = responseTime;
        return this;
    }

    public String getBody() {
        return body;
    }

    public RequestContent setBody(String body) {
        this.body = body;
        return this;
    }

    public String getExtra() {
        return extra;
    }

    public RequestContent setExtra(String extra) {
        this.extra = extra;
        return parseExtra();
    }

    public Map<String, String> getExtraMap() {
        return extraMap;
    }

    public void setExtraMap(Map<String, String> extraMap) {
        this.extraMap = extraMap;
    }

    /**
     * 解析扩展字段
     */
    private RequestContent parseExtra() {
        if (null != extra && !"".equals(extra)) {
            try {
                String[] keyValueArr = extra.split(";");
                if (keyValueArr.length > 0) {
                    Map<String, String> extraMap = getExtraMap() == null ? new HashMap<>() : getExtraMap();
                    String[] keyValue;
                    for (String s : keyValueArr) {
                        keyValue = s.split(":");
                        extraMap.put(keyValue[0], keyValue[1]);
                    }
                    setExtraMap(extraMap);
                }
            } catch (Exception e) {
                // 解析出错忽略本次扩展字段
            }
        }
        return this;
    }

    /**
     * 连接请求类型
     */
    public enum Type {
        /**
         * 请求
         */
        REQUEST,
        /**
         * 应答
         */
        RESPONSE
    }

    /**
     * 命名
     */
    public enum Cmd {
        /**
         * 心跳检测
         */
        HEART,
        /**
         * 应答服务器
         */
        ECHO
    }
}
