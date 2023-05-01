package com.xxx.netty.d1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * 2023/5/1
 **/

public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        //1.启动器类
        Channel channel = new Bootstrap()
                //2.添加组件 EventLoop
                .group(new NioEventLoopGroup())
                //选择客户端channel事件
                .channel(NioSocketChannel.class)
                //添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    //连接建立后调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new StringEncoder());
                    }
                    //连接到服务器
                }).connect(new InetSocketAddress(8080))
                .sync()
                .channel();
        System.out.println(channel);
        System.out.println("---------");
    }
}
