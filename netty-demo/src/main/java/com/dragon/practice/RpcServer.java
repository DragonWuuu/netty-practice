package com.dragon.practice;

import com.dragon.practice.protocol.LengthFrameDecoder;
import com.dragon.practice.protocol.MessageCodecSharable;
import com.dragon.practice.server.handler.RpcRequestHandler;
import com.dragon.practice.server.handler.ServerServiceHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcServer {
    public static void main(String[] args) {
        MultiThreadIoEventLoopGroup boss = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        MultiThreadIoEventLoopGroup workers = new MultiThreadIoEventLoopGroup(10, NioIoHandler.newFactory());
        LoggingHandler loggingHandler = new LoggingHandler();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        ChannelFuture serverFuture = new ServerBootstrap()
                .group(boss, workers)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast("exception handler", new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        log.error("客户端:{}，message:{}", ctx.channel().remoteAddress(), cause.getMessage());
                                    }
                                })
                                .addLast(new LengthFrameDecoder())
                                .addLast(loggingHandler)
                                .addLast(messageCodecSharable)
                                .addLast(new RpcRequestHandler());
                    }
                })
                .bind(9090);
        try {
            serverFuture.sync();
            serverFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }
}
