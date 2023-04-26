package com.xxx.test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.xxx.netty.util.ByteBufferUtil.debugAll;

/**
 * 2023/4/24
 * 粘包，半包
 * tcp采用Nagle算法，可能会出现粘包，
 * 然后滑动窗口+MTU限制就会出现拆包的问题
 **/

public class TestByteBufferExam {
    public static void main(String[] args) {
        ByteBuffer source = ByteBuffer.allocate(32);
        source.put("Hello,world\nI'm zhangsan\nHo".getBytes());
//        source.flip();
//        String s = StandardCharsets.UTF_8.decode(source).toString();
//        System.out.println(s);
//        System.out.println('\n');
//        System.out.println((char)0x0a);
        split(source);
        source.put("w are you?\n".getBytes());
        split(source);
    }

    private static void split(ByteBuffer source) {
        source.flip();
        for (int i = 0; i < source.limit(); i++) {
            // 找到一条完整消息
            if (source.get(i) == '\n') {
                int length = i + 1 - source.position();
                // 把这条完整消息存入新的 ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从 source 读，向 target 写
                for (int j = 0; j < length; j++) {
                    target.put(source.get());
                }
                target.flip();
                //打印测试
                String s = StandardCharsets.UTF_8.decode(target).toString();
                System.out.println(s);
                debugAll(target);
            }

        }
        source.compact();
    }
}
