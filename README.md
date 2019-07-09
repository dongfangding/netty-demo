# netty-demo

#### 介绍
netty学习，参考入门https://www.w3cschool.cn/netty4userguide/52ki1iey.html

更多概念讲解： https://www.w3cschool.cn/essential_netty_in_action/essential_netty_in_action-un8q288w.html

#### 包简介

* com.ddf.netty.quickstart.keepalive

基于TCP和自定义的数据格式以及编解码器实现的长连接功能，用来对数据进行接收和传送;监控连接变化和每个连接的消息队列，对消息进行持久化操作世（实际项目中会与数据库同步，例子中这一块就写的比较随便了，写到文件中的）；

* com.ddf.netty.quickstart.http

基于`Netty`提供的开箱即用的`http`相关的编解码器来实现http的服务端和客户端的通信，只写了核心代码，发送数据的一些方法没有花时间详细封装；