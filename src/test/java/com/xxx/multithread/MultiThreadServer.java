package com.xxx.multithread;

import com.sun.org.apache.bcel.internal.generic.Select;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static com.xxx.netty.util.ByteBufferUtil.debugAll;

/**
 * 2023/4/28
 **/

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        //线程名字改为“boss”
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress(8080));
        Selector boss = Selector.open();
        ssc.register(boss, SelectionKey.OP_ACCEPT);
        //创建固定数量的worker
        //Worker worker = new Worker("worker-0");
        //创建数组：多个worker 建议将worker的数量设置为cpu核心的数量 Runtime.getRuntime().availableProcessors()
        //如果IO较多，阿姆达尔定律
        Worker[] workers = new Worker[2];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i);
        }
        AtomicInteger index = new AtomicInteger();
        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{}", sc.getRemoteAddress());
                    log.debug("before register...{}", sc.getRemoteAddress());

                    workers[index.getAndIncrement() % workers.length].register(sc);
//                    sc.register(worker.selector,SelectionKey.OP_READ,null);//关联worker的selector中
                    log.debug("after register...{}", sc.getRemoteAddress());
                }
            }
        }
    }

    static class Worker implements Runnable {
        private Thread thread;
        private Selector selector;
        private String name;

        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();
        private volatile boolean start = false;

        public Worker(String name) {
            this.name = name;
        }

        //初始化线程和Selector
        public void register(SocketChannel sc) throws IOException {
            if (!start) {
                selector = Selector.open();
                thread = new Thread(this, name);
                thread.start();
                start = true;
            }
            //向队列中添加任务，但这个任务并没有立刻执行
            queue.add(() -> {
                try {
                    sc.register(selector, SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    e.printStackTrace();
                }
            });
            selector.wakeup(); //唤醒select方法
        }

        @Override
        public void run() {
            while (true) {
                SelectionKey key = null;
                try {
                    selector.select(); //worker-0 被唤醒一次
                    //获取boss线程中添加的任务，并判断是否获取成功
                    Runnable task = queue.poll();
                    if (task != null) {
                        //执行获取到的任务
                        task.run();
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(128);
                            SocketChannel sc = (SocketChannel) key.channel();
                            log.debug("read>{}", sc.getRemoteAddress());
                            int read = sc.read(buffer);
                            if (read == -1) {
                                log.debug("远程主机已正常关闭连接:{}", key);
                                key.cancel();
                                sc.close();
                                continue;
                            }
                            buffer.flip();
//                            debugAll(buffer);
                            String s = Charset.defaultCharset().decode(buffer).toString();
                            System.out.println("Client:" + s);
                        }
                    }
                } catch (IOException e) {
                    log.debug("远程主机已主动关闭连接:{}", key);
                    //client主动断开时（异常关闭），将key进行cancel，反注册
                    key.cancel();
//                    e.printStackTrace();
                }
            }
        }
    }
}
