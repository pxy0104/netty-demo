package com.xxx.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 2023/4/24
 **/

public class TestGatheringWrites {
    public static void main(String[] args) {
        ByteBuffer a = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b = StandardCharsets.UTF_8.encode("world");
        ByteBuffer c = StandardCharsets.UTF_8.encode("世界");
        try (RandomAccessFile file = new RandomAccessFile("wordget.txt", "rw")) {
            FileChannel channel = file.getChannel();
            channel.write(new ByteBuffer[]{a,b,c});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        /*file.getChannel();*/
    }
}
