package com.xxx.netty.client.handler;


import com.xxx.netty.message.RpcRequestMessage;
import com.xxx.netty.message.RpcResponseMessage;
import com.xxx.netty.server.service.HelloService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    //                   序号
    public static final Map<Integer, Promise<Object>> PROMISES = new ConcurrentHashMap<>();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug("msg:{}", msg);
        Promise<Object> promise = PROMISES.remove(msg.getSequenceId());
        if (promise != null) {
            if (msg.getExceptionValue() != null) {
                promise.setFailure(msg.getExceptionValue());
            } else {
                promise.setSuccess(msg.getReturnValue());
            }
        }
    }
}
