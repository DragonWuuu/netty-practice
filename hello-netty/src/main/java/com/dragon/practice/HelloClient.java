package com.dragon.practice;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

@Slf4j
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {
        MultiThreadIoEventLoopGroup group = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(
                        new ChannelInitializer<>() {
                            @Override
                            protected void initChannel(Channel channel) throws Exception {
                                channel.pipeline()
                                        .addLast(new LoggingHandler(LogLevel.DEBUG))
                                        .addLast(new StringEncoder());
                            }
                        }
                )
                .connect(new InetSocketAddress("localhost", 8080));

        Channel channel = channelFuture
                // 等待连接建立
                .sync()
                .channel();

        // 使用监听者模式，对比起sync()方法，这个不会阻塞主线程，operationComplete方法在NIO线程执行
//        channelFuture.addListener(new ChannelFutureListener() {
//            @Override
//            public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                Channel channel = channelFuture.channel();
//                channel.writeAndFlush("hello,world");
//            }
//        });
        // 创建一个新的Thread，将console的消息发送到服务器，如果输入为q，则退出。
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String line = scanner.nextLine();
                if (line.equals("q")){
                    // 异步操作
                    channel.close();
                    log.info("close?");
                    break;
                }
                // 发送数据
                channel.writeAndFlush(line);
            }
        }, "scanner").start();

        // 使用CloseFuture在channel关闭后执行代码
        ChannelFuture closeFuture = channel.closeFuture();
        log.info("wait for channel close...");

        // 使用监听器的方式
        closeFuture.addListener(future -> {
            log.info("listened channel closed");
        });

        // 使用同步的方式
        closeFuture.sync();
        log.info("sync channel closed");

        group.shutdownGracefully();
    }
}
