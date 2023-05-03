package com.xxx.netty.d3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.nio.charset.Charset;

/**
 * 2023/5/3
 **/

public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(1024,0,4,0,4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        send(buf, "hello, world");
        send(buf, "world");
        send(buf, "hello, zhangsan");
        channel.writeInbound(buf);
    }

    private static void send(ByteBuf buf, String str) {
        int len = str.length();
        buf.writeInt(len); //写入长度

        buf.writeCharSequence(str, Charset.defaultCharset()); //写入内容
    }
}
