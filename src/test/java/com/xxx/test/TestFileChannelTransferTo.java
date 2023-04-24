package com.xxx.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 2023/4/24
 **/

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        long start = System.nanoTime();
        try {
            FileInputStream from = new FileInputStream("words.txt");
            FileOutputStream to = new FileOutputStream("words_bak.txt");
            FileChannel fromChannel = from.getChannel();
            FileChannel toChannel = to.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        System.out.println("transferTo 用时：" + (end - start) / 1000000.0);
    }
}
