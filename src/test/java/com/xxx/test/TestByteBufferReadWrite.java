package com.xxx.test;

import java.nio.ByteBuffer;

import static com.xxx.netty.util.ByteBufferUtil.debugAll;

/**
 * 2023/4/23
 **/

public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);

        debugAll(buffer);
        buffer.put(new byte[]{0x62,0x63,0x64});
        debugAll(buffer);

//        System.out.println(buffer.get()); //读取当前位置
//        System.out.println(buffer.get()); //读取当前位置
        //切换读模式
        buffer.flip();
        System.out.println((char)buffer.get());
        debugAll(buffer);
        buffer.compact();
        debugAll(buffer);
        buffer.put((byte)0x66);
        debugAll(buffer);
    }
}
