package com.xxx.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;

/**
 * 2023/5/1
 **/

public class HelloServer {
    public static void main(String[] args) {
        //1.启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2.Boss\WorkerEventLoop(selector,thread) group组
                .group(new NioEventLoopGroup())
                //3.ServerSocketChannel的实现
                .channel(NioServerSocketChannel.class)
                //4.boss负责处理连接  worker(child)负责处理读写，决定了worker(child)能执行哪些操作handler
                .childHandler(
                        //channel 代表客户端进行数据读写的通道
                        //Initializer初始化，负责添加别的通道
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel ch) {
                                //添加具体的handler   将ByteBuf转换为字符串
                                ch.pipeline().addLast(new StringDecoder());
                                //自定义handler
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    //处理读事件
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                打印转换好的字符串
                                        System.out.println(msg);
                                    }
                                });
                            }
                        }).bind(8080);
    }
}
