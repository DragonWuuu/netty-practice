package com.dragon.practice;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketException;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

@Slf4j
// 一个回声服务器，客户端发什么回什么
public class EchoServer {
    public static void main(String[] args) {
        // 开启一个Netty server
        MultiThreadIoEventLoopGroup parentGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        MultiThreadIoEventLoopGroup childGroup = new MultiThreadIoEventLoopGroup(2, NioIoHandler.newFactory());
        ChannelFuture channelFuture = new ServerBootstrap()
                .group(parentGroup,
                        childGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LocalEventLoopGroup(1))
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        printBuffer(((ByteBuf) msg));
                                        ctx.channel().writeAndFlush(msg);
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                            log.error("error remote:{}, {}, message: {}",
                                                    ctx.channel().remoteAddress(),
                                                    cause.getClass(),
                                                    cause.getMessage());
                                    }
                                })
                                .addLast(new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        super.write(ctx, msg, promise);
                                    }
                                });
                    }
                })
                .bind(8080);
        try {
            channelFuture.sync();
            Channel channel = channelFuture.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        finally {
            parentGroup.shutdownGracefully();
            childGroup.shutdownGracefully();
        }
    }
    public static void printBuffer(ByteBuf byteBuf) {
        int length = byteBuf.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 :1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2);
        buf.append("read index:").append(byteBuf.readerIndex());
        buf.append(" write index:").append(byteBuf.writerIndex());
        buf.append(" capacity:").append(byteBuf.capacity());
        buf.append(NEWLINE);
        appendPrettyHexDump(buf, byteBuf);
        System.out.println(buf.toString());
    }
}
