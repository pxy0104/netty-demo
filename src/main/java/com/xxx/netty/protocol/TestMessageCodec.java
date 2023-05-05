package com.xxx.netty.protocol;

import com.xxx.netty.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * 2023/5/3
 **/

public class TestMessageCodec {
    //FRAME_DECODER是一个状态量，不可以抽离出来作为一个变量，在多线程下不安全
//    public static final ProcotolFrameDecoder FRAME_DECODER = new ProcotolFrameDecoder(
//            1024,
//            12,
//            4,
//            0,
//            0);
    public static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();

    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                LOGGING_HANDLER,
                new ProtocolFrameDecoder(
                        1024,
                        12,
                        4,
                        0,
                        0),
                //自定义编解码器
                new MessageCodec()
        );
        LoginRequestMessage message = new LoginRequestMessage("tom", "3096");

        //channel.writeOutbound(message);
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buf);
        System.out.println("可读字节数:" + buf.readableBytes());
        ByteBuf s1 = buf.slice(0, 100);
        ByteBuf s2 = buf.slice(100, buf.readableBytes() - 100);
        s1.retain(); //引用计数 +1
        channel.writeInbound(s1);
        channel.writeInbound(s2);
    }
}
