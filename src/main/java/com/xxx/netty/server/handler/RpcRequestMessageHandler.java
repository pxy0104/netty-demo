package com.xxx.netty.server.handler;

import com.xxx.netty.message.RpcRequestMessage;
import com.xxx.netty.message.RpcResponseMessage;
import com.xxx.netty.server.service.HelloService;
import com.xxx.netty.server.service.ServicesFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage message) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        try {
            //根据接口全名找到类的实现
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(message.getInterfaceName()));
            Method method = service.getClass().getMethod(message.getMethodName(), message.getParameterTypes());
            Object invoke = method.invoke(service, message.getParameterValue());
//            System.out.println(invoke);
            response.setReturnValue(invoke);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ctx.writeAndFlush(response);
    }

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcRequestMessage message = new RpcRequestMessage(
                1,
                "com.xxx.netty.server.service.HelloService",
                "sayHello",
                String.class,
                new Class[]{String.class},
                new Object[]{"张三丰"}
        );
    }
}
