package com.xxx.netty.d2;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * 2023/5/2
 **/
@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        EventLoop eventLoop = new NioEventLoopGroup().next();

        DefaultPromise<Object> promise = new DefaultPromise<>(eventLoop);
        new Thread(()->{
            int a = 0;
            //启动线程进行处理，计算完毕后向promise填充结果
            System.out.println("开始计算");
            try {
                Thread.sleep(1000);
                 a = 1+9;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            promise.setSuccess(a);
        }).start();

        log.debug("等待结果...");
        log.debug("结果是:{}",promise.get());

    }
}
