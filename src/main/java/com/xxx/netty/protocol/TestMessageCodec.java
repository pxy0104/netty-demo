package com.xxx.netty.protocol;

import io.netty.channel.embedded.EmbeddedChannel;

/**
 * 2023/5/3
 **/

public class TestMessageCodec {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(
                new MessageCodec()
        );

    }
}
