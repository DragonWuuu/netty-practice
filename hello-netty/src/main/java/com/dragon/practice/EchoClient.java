package com.dragon.practice;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class EchoClient {
    public static void main(String[] args) {
        // 创建客户端服务器
        MultiThreadIoEventLoopGroup group = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        log.error("error remote:{},{},{}", ctx.channel().remoteAddress(), cause.getClass(), cause.getMessage());
                                    }
                                })
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(new LoggingHandler());
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost", 8080));
        try {
            channelFuture.sync();
            Channel channel = channelFuture.channel();
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

            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }

    }
}
