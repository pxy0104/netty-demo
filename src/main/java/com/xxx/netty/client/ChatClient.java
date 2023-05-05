package com.xxx.netty.client;


import com.xxx.netty.message.*;
import com.xxx.netty.protocol.MessageCodecSharable;
import com.xxx.netty.protocol.ProtocolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
        public static void main(String[] args) {
            NioEventLoopGroup group = new NioEventLoopGroup();
            LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
            MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
            CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
            AtomicBoolean LOGIN = new AtomicBoolean(false);
            AtomicBoolean EXIT = new AtomicBoolean(false);

            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.channel(NioSocketChannel.class);
                bootstrap.group(group);
                bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameDecoder());
//                    ch.pipeline().addLast(LOGGING_HANDLER);
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        //心跳数据包发送，验证机器存活
                        //3s内没有发送channel的数据，会触发一个IdleState#WRITER_IDLE;
                        ch.pipeline().addLast(new IdleStateHandler(0,3,0));
                        ch.pipeline().addLast(new ChannelDuplexHandler(){
                            @Override
                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                IdleStateEvent event = (IdleStateEvent) evt;
                                //检测到懒惰事件发生
                                if (event.state() == IdleState.WRITER_IDLE) {
//                                log.debug("已经3秒没有写消息，发送一个心跳包");
                                    ctx.writeAndFlush(new PingMessage());
                                }
                            }
                        });
                        ch.pipeline().addLast("client handler", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                new Thread(() -> {
                                    Scanner scan = new Scanner(System.in);
                                    System.out.println("请输入用户名");
                                    if(EXIT.get()){
                                        return;
                                    }
                                    String username = scan.nextLine();
                                    System.out.println("请输入密码");
                                    if(EXIT.get()){
                                        return;
                                    }
                                    String password = scan.nextLine();
                                    //构造消息对象
                                    LoginRequestMessage message = new LoginRequestMessage(username, password);
                                    //发送消息
                                    ctx.writeAndFlush(message);
                                    System.out.println("等待***菜单");

                                    try {//登录响应，唤醒 ctl唤醒
                                        WAIT_FOR_LOGIN.await();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (!LOGIN.get()) {
                                        //如果登录失败
                                        ctx.channel().close();
                                        return;
                                    }
                                    //若登录成功
                                    while (true) {
                                        System.out.println("==================================");
                                        System.out.println("send [username] [content]");
                                        System.out.println("gsend [group name] [content]");
                                        System.out.println("gcreate [group name] [m1,m2,m3...]");
                                        System.out.println("gmembers [group name]");
                                        System.out.println("gjoin [group name]");
                                        System.out.println("gquit [group name]");
                                        System.out.println("quit");
                                        System.out.println("==================================");
                                        String command = scan.nextLine();
                                        if(EXIT.get()){
                                            return;
                                        }
                                        String[] s = command.split(" ");
                                        switch (s[0]) {
                                            case "send":
                                                ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                                break;
                                            case "gsend":
                                                ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                                break;
                                            case "gcreate":
                                                Set<String> set = new HashSet(Arrays.asList(s[2].split(",")));
                                                set.add(username);
                                                ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                                break;
                                            case "gmembers":
                                                ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                                break;
                                            case "gjoin":
                                                ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                                break;
                                            case "gquit":
                                                ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                                break;
                                            case "quit":
                                                ctx.channel().close();
                                                return;
                                        }
                                    }

                                }, "system in").start();
                            }

                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                log.debug("response msg:{}", msg);
                                if (msg instanceof LoginResponseMessage) {
                                    LoginResponseMessage response = (LoginResponseMessage) msg;
                                    if (response.isSuccess()) {
                                        LOGIN.set(true);
                                    }
                                    //唤醒system in线程
                                    WAIT_FOR_LOGIN.countDown();
                                }
                            }
                            //在连接断开时触发
                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                log.debug("连接已经断开，按任意键退出..");
                                EXIT.set(true);
                            }
                            // 在出现异常时触发
                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                log.debug("连接已经断开，按任意键退出..{}", cause.getMessage());
                                EXIT.set(true);
                            }
                        });
                    }
                });
                Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
                channel.closeFuture().sync();
            } catch (Exception e) {
                log.error("client error", e);
            } finally {
                group.shutdownGracefully();
            }
        }
}
