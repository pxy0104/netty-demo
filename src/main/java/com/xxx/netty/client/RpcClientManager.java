package com.xxx.netty.client;

import com.xxx.netty.client.handler.RpcResponseMessageHandler;
import com.xxx.netty.message.RpcRequestMessage;
import com.xxx.netty.protocol.MessageCodecSharable;
import com.xxx.netty.protocol.ProtocolFrameDecoder;
//import com.xxx.netty.protocol.SequenceIdGenerator;
import com.xxx.netty.protocol.SequenceIdGenerator;
import com.xxx.netty.server.service.HelloService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.Objects;

@Slf4j
public class RpcClientManager {

    private static final Object LOCK = new Object();

    private static Channel channel = null;
    public static Channel getChannel(){
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) {
            if (channel != null) {  //双检锁
                return channel;
            }
            initChannel();
            return channel;
        }
    }
    public static void main(String[] args) {
        HelloService service = getProxyService(HelloService.class);
        service.sayHello("张三");
        service.sayHello("ls");
        service.sayHello("ww");
//        getChannel().writeAndFlush(new RpcRequestMessage(
//                1,
//                "com.xxx.netty.server.service.HelloService",
//                "sayHello",
//                String.class,
//                new Class[]{String.class},
//                new Object[]{"张三"}));
    }

    //创建一个代理类
    public static <T> T getProxyService(Class<T> serviceClass){
        ClassLoader loader = serviceClass.getClassLoader();
        Class[] interfaces = new Class[]{serviceClass};
        //使用jdk的代理
        Object o = Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) -> {
            int sequenceId = SequenceIdGenerator.nextId();
            RpcRequestMessage msg = new RpcRequestMessage(
                    sequenceId,
                    serviceClass.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args);
            //将消息发送出去
            getChannel().writeAndFlush(msg);
            //指定promise来接收消息
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId,promise);
            promise.await(); //等待结果返回，然后进行下一步
            if (promise.isSuccess()) {
                return promise.getNow();
            } else {

                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }

    // 初始化Channel 方法
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtocolFrameDecoder());
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(RPC_HANDLER);
                }
            });
            channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}
