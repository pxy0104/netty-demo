package com.xxx.netty.d3;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;

import static com.xxx.netty.d3.TestByteBuf.log;

/**
 * 2023/5/3
 **/

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{1,2,3,'d','e','f','g','h','i','j'});
//        log(buf);

        System.out.println(ByteBufUtil.prettyHexDump(buf));

        ByteBuf slice1 = buf.slice(0,5);
        ByteBuf slice2 = buf.slice(5,5);

        log(slice1);
        log(slice2);
        System.out.println();
    }
}
