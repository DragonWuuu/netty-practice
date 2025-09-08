package com.dragon.practice;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class TestConnectRedis {
    public static void main(String[] args) throws InterruptedException {
        byte[] LINE = new byte[]{13, 10};
        ChannelFuture channelFuture = new Bootstrap()
                .group(new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory()))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler())
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        /*
                                         * set name zhangsan
                                         * 先指定多少个数组元素（每个词视为一个元素） *3
                                         * 再指定告诉它第一个元素的长度 $3
                                         * 再告诉它内容 set
                                         * 同上面两步 $4
                                         * name
                                         * $8
                                         * zhangsan
                                         */
                                        ByteBuf buffer = ctx.alloc().buffer();
                                        buffer.writeBytes("*3".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("$3".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("set".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("$4".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("name".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("$8".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("zhangsan".getBytes());
                                        buffer.writeBytes(LINE);

                                        // get name
                                        buffer.writeBytes("*2".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("$3".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("get".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("$4".getBytes());
                                        buffer.writeBytes(LINE);
                                        buffer.writeBytes("name".getBytes());
                                        buffer.writeBytes(LINE);
                                        ctx.writeAndFlush(buffer);
                                    }
                                });
                    }
                })

                // redis
                .connect(new InetSocketAddress("localhost", 6379));
        channelFuture.sync();
        Channel channel = channelFuture.channel();

    }
}
