package com.xxx.netty.d2;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 2023/5/2
 **/

@Slf4j
public class TestJdkFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

//        线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> future = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("执行计算");
                Thread.sleep(1000);
                return 50;
            }});

        //3.主线程通过future来获取结果
        log.debug("等待结果");
        log.debug("执行结果:{}", future.get());
    }
}
