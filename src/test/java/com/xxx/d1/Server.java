package com.xxx.d1;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

import static com.xxx.util.ByteBufferUtil.debugRead;


/**
 * 2023/4/26
 **/
@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        //使用nio来理解阻塞模式，单线程
        //0.ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //1.创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //1.1切换为非阻塞模式：使得accept方法为非阻塞模式
        //如果没有连接，ssc.accept返回null.
        ssc.configureBlocking(false);
        //2.绑定监听端口
        ssc.bind(new InetSocketAddress(8080));
        //3.SocketChannel连接集合
        List<SocketChannel> channels = new ArrayList<>();

        while (true) {
            //监听端口，阻塞
            SocketChannel sc = ssc.accept();
            if (sc != null) {
                log.debug("connected...{}", sc);
                channels.add(sc);
                sc.configureBlocking(false);
            }
            for (SocketChannel channel : channels) {
                //接收客户端发送的数据
                int read = channel.read(buffer);//如果没有读到数据，返回0
                if (read > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read ...{}", channel);
                }
            }
        }
    }
}
