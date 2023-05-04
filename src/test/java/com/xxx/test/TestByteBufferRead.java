package com.xxx.test;

import java.nio.ByteBuffer;

import static com.xxx.util.ByteBufferUtil.debugAll;

/**
 * 2023/4/24
 **/

public class TestByteBufferRead {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        debugAll(buffer);
        buffer.put(new byte[]{'a','b','c','d'});
        buffer.flip();
        //从头开始读
        buffer.get(new byte[4]);

        debugAll(buffer);
        buffer.rewind();
        System.out.println((char)buffer.get());

    }
}
