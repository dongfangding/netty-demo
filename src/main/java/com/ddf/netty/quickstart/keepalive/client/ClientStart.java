package com.ddf.netty.quickstart.keepalive.client;

import com.ddf.netty.quickstart.keepalive.server.RequestInfo;

import java.util.UUID;

public class ClientStart {
    public static void main(String[] args) throws InterruptedException {
//        Scanner input = new Scanner(System.in);
//        Client bootstrap = new Client(8080, "127.0.0.1");
//
//        String infoString = "";
//        while (true) {
//            infoString = input.nextLine();
//            RequestInfo req = new RequestInfo();
//            req.setType((byte) 1);
//            req.setInfo(infoString);
//            bootstrap.sendMessage(req);
//        }

        Client bootstrap = new Client(8080, "127.0.0.1");

        RequestInfo req = new RequestInfo();
        req.setType((byte) 1);
        req.setInfo(UUID.randomUUID().toString());
        bootstrap.sendMessage(req);
        System.out.println("=============");
        Thread.sleep(5000);
        req.setInfo(UUID.randomUUID().toString());
        bootstrap.sendMessage(req);
//        bootstrap.close(bootstrap.getSocketChannel());
    }
}