package com.xxx.netty.d1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * 2023/5/1
 **/
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {

        NioEventLoopGroup group = new NioEventLoopGroup();
        //1.启动器类
        ChannelFuture channelFuture = new Bootstrap()
                //2.添加组件 EventLoop
                .group(group)
                //选择客户端channel事件
                .channel(NioSocketChannel.class)
                //添加处理器
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    //连接建立后调用
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        ch.pipeline().addLast(new StringEncoder());
                    }
                    //连接到服务器 异步非阻塞
                }).connect(new InetSocketAddress(8080));
//        Thread.sleep(1000);
        channelFuture.sync();
        Channel channel = channelFuture.channel();
//        System.out.println(channel);
//        log.debug("{}",channel);
//        channel.writeAndFlush("123");
        System.out.println("---------");
//        channelFuture.addListener(new ChannelFutureListener() {
//            @Override   //在nio线程建立连接之后，执行此方法内的代码 相当于sync()
//            public void operationComplete(ChannelFuture future) throws Exception {
//                Channel channel = channelFuture.channel();
////                channel.writeAndFlush("123");
//            }
//        });
        new Thread(()->{
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.next();
                if ("q".equals(line)) {
                    channel.close(); //nio线程操作
                    //关闭发生以后处理操作 ClosedFuture
                    break;
                }
                channel.writeAndFlush(line);
            }
        }).start();

        ChannelFuture closeFuture = channel.closeFuture();//同步处理关闭
//        System.out.println("waiting close...");
//        closeFuture.sync();
//        log.debug("关闭之后的操作");
        //异步处理关闭
        closeFuture.addListener((ChannelFutureListener) future -> {
            System.out.println("waiting close...");
//                closeFuture.sync();
            log.debug("关闭之后的操作");
            group.shutdownGracefully();
        });
    }
}
