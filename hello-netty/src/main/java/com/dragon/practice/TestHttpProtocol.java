package com.dragon.practice;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

@Slf4j
public class TestHttpProtocol {
    public static void main(String[] args) throws InterruptedException {
        ChannelFuture channelFuture = new ServerBootstrap()
                .group(new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory()))
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new LoggingHandler())
                                .addLast(new HttpServerCodec())
                                .addLast(new SimpleChannelInboundHandler<DefaultHttpRequest>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, DefaultHttpRequest msg) throws Exception {
                                        DefaultFullHttpResponse response =
                                                new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                        byte[] content = "<h1>hello, Netty</h1>".getBytes();
                                        response.headers().setInt(CONTENT_LENGTH, content.length);
                                        response.content().writeBytes(content);

                                        ctx.writeAndFlush(response);
                                    }
                                })
                        ;
                    }
                })
                .bind(8080);
        channelFuture.sync();
        Channel channel = channelFuture.channel();
    }
}
