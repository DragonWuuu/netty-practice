package com.dragon.practice;

import com.dragon.practice.protocol.LengthFrameDecoder;
import com.dragon.practice.protocol.MessageCodecSharable;
import com.dragon.practice.server.handler.RpcResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class RpcClient {

    private static Channel clientChannel;
    private static Object LOCK = new Object();
    private static final LoggingHandler loggingHandler = new LoggingHandler();
    private static final MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
    private static final RpcResponseHandler rpcResponseHandler = new RpcResponseHandler();

    public static Channel getClient(){
        if (clientChannel != null) return clientChannel;
        synchronized (LOCK) {
            if (clientChannel != null) return clientChannel;
            Channel channel = initChannel();
            clientChannel = channel;
        }
        return clientChannel;
    }

    private static Channel initChannel(){
        ChannelFuture channelFuture = new Bootstrap()
                .group(new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory()))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline()
                                .addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        log.error("服务器:{}，message:{}", ctx.channel().remoteAddress(), cause.getMessage());
                                    }
                                })
//                                .addLast(loggingHandler)
                                .addLast(new LengthFrameDecoder())
                                .addLast(messageCodecSharable)
                                .addLast(rpcResponseHandler);
                    }
                })
                .connect(new InetSocketAddress("localhost", 9090));
        try {
            Channel channel = channelFuture.sync().channel();
            return channel;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Promise putPromise(Integer sequenceId, Promise promise) {
        return rpcResponseHandler.put(sequenceId, promise);
    }
    
}
