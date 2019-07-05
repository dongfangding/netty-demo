package com.ddf.netty.quickstart.keepalive.server;

public class RequestInfo {

    private String info;
    private byte type;

    public void setInfo(String info) {
        this.info = info;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public byte getType() {
        return type;
    }
}
