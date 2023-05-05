package com.xxx.netty.protocol;

import com.xxx.netty.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * 2023/5/3
 **/
@Slf4j
@ChannelHandler.Sharable
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {
        int magicNum = in.readInt(); //4
        byte version = in.readByte();
        int serializerType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);
//        if (serializerType == 0) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Message message = (Message) ois.readObject();
//        }
        log.debug("{},{},{},{},{},{}", magicNum, version, serializerType, messageType, sequenceId, length);
        log.debug("{}", message);
        out.add(message); //传递给下一个handler使用
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //1. 魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        //2. 1字节版本
        out.writeByte(1);
        //3. 1字节的序列化方式 0:jdk方式 1:json
        out.writeByte(0);
        //4. 1字节的指令类型
        out.writeByte(msg.getMessageType());
        //5. 4字节请求序号
        out.writeInt(msg.getSequenceId());
        //填充16个字节，无意义
        out.writeByte(0xff);
        //6. 消息内容字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        //7. 长度
        out.writeInt(bytes.length);
        //8. 写入内容
        out.writeBytes(bytes);
    }
}
