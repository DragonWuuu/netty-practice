package com.dragon.practice;

import com.dragon.practice.protocol.LengthFrameDecoder;
import com.dragon.practice.protocol.MessageCodecSharable;
import com.dragon.practice.server.handler.ClientServiceHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class InteractClient {
    public static void main(String[] args) {
        LoggingHandler loggingHandler = new LoggingHandler();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        MultiThreadIoEventLoopGroup worker = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        ChannelFuture clientFuture = new Bootstrap()
                .group(worker)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast("exception handler", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        log.error("服务器:{}，message:{}", ctx.channel().remoteAddress(), cause.getMessage());
                                    }
                                })
                                .addLast(new LengthFrameDecoder())
                                .addLast(loggingHandler)
                                .addLast(messageCodecSharable)
                                .addLast(new ClientServiceHandler());
                    }
                })
                .connect(new InetSocketAddress("localhost", 8080));
        try {
            clientFuture.sync();
            clientFuture.channel().closeFuture().sync();
        } catch (
                InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            worker.shutdownGracefully();
        }
    }
}

