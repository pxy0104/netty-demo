package com.xxx.multithread;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * 2023/4/28
 **/

public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress(8080));
        sc.write(Charset.defaultCharset().encode("12345c"));
//        sc.close();
        System.in.read();
    }
}
