package com.xxx.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.NettyRuntime;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 2023/5/1
 **/

@Slf4j
public class TestEventLoop {
    public static void main(String[] args) {
        //1.创建事件循环组
        EventLoopGroup group = new NioEventLoopGroup(2);
//        System.out.println(NettyRuntime.availableProcessors());

        /*group.next().submit(() -> {
            //普通任务，异步处理
            log.debug("{ok}");
        });*/


        //定时任务
        group.next().scheduleAtFixedRate(()->{
            log.debug("schedule task");
        },0,1, TimeUnit.SECONDS);
        log.debug("main");
    }

}
