package com.xxx.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 2023/4/24
 **/

public class TestFileStream {
    public static void main(String[] args) {
        try {
            FileInputStream fileInputStream = new FileInputStream("words.txt");
            FileChannel channel = fileInputStream.getChannel();
//            ByteBuffer buffer = ByteBuffer.allocate(10);
//            buffer.put(new byte[]{'a','b'});
//            channel.write(buffer);
//            System.out.println(channel.read(buffer));
            int b;
            while ((b = fileInputStream.read()) != -1) {
                System.out.print((char) b);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
