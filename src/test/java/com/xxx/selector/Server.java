package com.xxx.selector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;


/**
 * 2023/4/26
 * 事件类型：accept connect read write
 **/
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //创建 selector，管理多个channel
        Selector selector = Selector.open();

        //0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //1.1切换为非阻塞模式：使得accept方法为非阻塞模式
        //如果没有连接，ssc.accept返回null.
        ssc.configureBlocking(false);

        //把channel注册到selector/
        SelectionKey sscKey = ssc.register(selector, SelectionKey.OP_ACCEPT, null);
        //key只关注accept事件
//        sscKey.interestOps();
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            //3. select 方法,没有事件发生，线程阻塞，有事件，线程执行
            selector.select();//如果事件未处理（也未取消时key.cancel()）时，重新加入集合
//4.处理事件,返回事件集合selectionKeys
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
//            Iterator<SelectionKey> iter = selectionKeys.iterator();

            while (iter.hasNext()) {
                SelectionKey key = iter.next();
//                将iterator当前的key移除，确保选中事件执行完移除
                iter.remove();
                log.debug("key: {}", key);
                //5.区分事件类型
                if (key.isAcceptable()) {
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    SocketChannel sc = channel.accept();
                    sc.configureBlocking(false);
                    //sc注册到selector中，并设置事件类型
                    SelectionKey scKey = sc.register(selector, SelectionKey.OP_READ, null);
//                    scKey.interestOps();
                    log.debug("来源-Client:{}", sc);
                } else if (key.isReadable()) {
                    try {
                        SocketChannel channel = (SocketChannel) key.channel();
                        int read = channel.read(buffer);//如果是正常断开，read返回-1
                        if (read == -1) {
                            //正常断开，进行反注册
                            log.debug("远程主机已正常关闭连接:{}", key);
                            key.cancel();
                            channel.close();
                        } else {
                            buffer.flip();
//                            debugRead(buffer);
                            String str = Charset.defaultCharset().decode(buffer).toString();
                            //清空缓冲区
                            buffer.clear();
                            System.out.println("Client: " + str);
                        }
                    } catch (IOException e) {
                        log.debug("远程主机已主动关闭连接:{}", key);
                        //client主动断开时（异常关闭），将key进行cancel，反注册
                        key.cancel();
                    }
                }
            }
        }
    }
}
