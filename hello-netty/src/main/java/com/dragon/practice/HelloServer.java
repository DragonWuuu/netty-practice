package com.dragon.practice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloServer {
    public static void main(String[] args){
        MultiThreadIoEventLoopGroup boss = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        MultiThreadIoEventLoopGroup worker = new MultiThreadIoEventLoopGroup(4, NioIoHandler.newFactory());

        try {
            ChannelFuture channelFuture = new ServerBootstrap()
                    .group(
                            // boss
                            boss,
                            // worker
                            worker
                    )
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("StringDecoder", new StringDecoder())
                                    .addLast(new ChannelInboundHandlerAdapter() {
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            log.info("{}", msg);
                                            ctx.channel().writeAndFlush(ctx.alloc().buffer().writeBytes("...".getBytes()));
                                            ctx.writeAndFlush("...");
                                            super.channelRead(ctx, msg);
                                        }

                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            log.info("active: {}", ctx.channel());
                                        }
                                    });
                        }
                    })
                    .bind(8080);
            // 等待服务建立完成
            channelFuture.sync();
            // 等待channel结束事件
            Channel channel = channelFuture.channel();
            channel.closeFuture().sync();
            ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            // 优雅地关闭boss和worker
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}