package com.xxx.test;


import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


/**
 * 2023/4/23
 **/

@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        try (FileChannel channel = new FileInputStream("data.txt").getChannel();){
            //准备一个缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(3);
            int len = 0;
            while ((len = channel.read(buffer)) != -1) {
                //从这个通道中读入一个字节序列到给定的缓冲区

                log.debug("读取的长度：{}",len);

                //打印buffer的内容
                buffer.flip(); //切换至读模式
                while (buffer.hasRemaining()){
//                    byte b = buffer.get();
//                    log.debug("实际打印{}:",(char)b);
                    Charset charset = Charset.forName("UTF-8");
                    CharsetDecoder decoder = charset.newDecoder();
                    System.out.println(decoder.decode(buffer).toString());
                }
                buffer.clear();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
