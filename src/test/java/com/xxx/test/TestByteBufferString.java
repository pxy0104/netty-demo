package com.xxx.test;

import com.google.common.base.Utf8;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static com.xxx.test.ByteBufferUtil.debugAll;

/**
 * 2023/4/24
 **/

public class TestByteBufferString {
    public static void main(String[] args) {
        //1.将字符串转为ByteBuffer
        String hello = "hello";
        byte[] bytes = hello.getBytes(StandardCharsets.UTF_8);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.put(bytes);
        //切换到读模式
        buffer.flip();
//        debugAll(buffer);

        //2.Charset
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode(hello);
//        debugAll(buffer1);
//        buffer1.get();
//        debugAll(buffer1);

        //3.wrap
        ByteBuffer buffer2 = ByteBuffer.wrap(hello.getBytes());

//        System.out.println(buffer.toString());
        //切换到读模式再进行转换
        String h = StandardCharsets.UTF_8.decode(buffer1).toString();
        System.out.println(h);

    }

}
