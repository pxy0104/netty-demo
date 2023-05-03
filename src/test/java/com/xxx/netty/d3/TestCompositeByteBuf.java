package com.xxx.netty.d3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;

import java.nio.charset.Charset;

import static com.xxx.netty.d3.TestByteBuf.log;

/**
 * 2023/5/3
 **/

public class TestCompositeByteBuf {
    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.buffer();
        buf1.writeCharSequence("abc", Charset.defaultCharset());
        buf2.writeCharSequence("def", Charset.defaultCharset());
        CompositeByteBuf buf = ByteBufAllocator.DEFAULT.compositeBuffer();
        buf.addComponents(true,buf1,buf2);
        log(buf);
        System.out.println(ByteBufUtil.prettyHexDump(buf));
    }
}
