package com.xxx.selectortlv;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * 2023/4/26
 **/

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(8080));
        //如果数据长度过长，服务端（对于以\n分割）可能接收不到全部，数据读取不全
        sc.write(StandardCharsets.UTF_8.encode("helloworld!nihao123456\n"));
        System.in.read();
        System.out.println("waiting...");
    }
}
