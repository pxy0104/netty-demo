package com.xxx.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static com.xxx.test.ByteBufferUtil.debugAll;

/**
 * 2023/4/24
 **/

public class TestScatteringReads {
    public static void main(String[] args) {
        try (FileChannel channel = new RandomAccessFile("words.txt", "r").getChannel()) {
            ByteBuffer a = ByteBuffer.allocate(3);
            ByteBuffer b = ByteBuffer.allocate(3);
            ByteBuffer c = ByteBuffer.allocate(5);
            ByteBuffer[] buffers = new ByteBuffer[]{a, b, c};
            channel.read(buffers);
            a.flip();
            b.flip();
            c.flip();
            debugAll(a);
            debugAll(b);
            debugAll(c);
//            channel.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
