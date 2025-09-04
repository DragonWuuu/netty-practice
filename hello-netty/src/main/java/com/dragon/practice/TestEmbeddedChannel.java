package com.dragon.practice;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestEmbeddedChannel {
    public static void main(String[] args) {
        // 预先写一个Handler
        // 仅入站Handler
        ChannelInboundHandlerAdapter h_1_inbound = new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.info("h_1_inbound");
                super.channelRead(ctx, msg);
            }
        };
        // 仅入站Handler
        ChannelInboundHandlerAdapter h_2_inbound = new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.info("h_2_inbound");
                super.channelRead(ctx, msg);
            }
        };
        // 仅出站Handler
        ChannelOutboundHandlerAdapter h_3_outbound = new ChannelOutboundHandlerAdapter(){
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.info("h_3_outbound");
                super.write(ctx, msg, promise);
            }
        };
        // 仅出站Handler
        ChannelOutboundHandlerAdapter h_4_outbound = new ChannelOutboundHandlerAdapter(){
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.info("h_4_outbound");
                super.write(ctx, msg, promise);
            }
        };

        // 获取EmbeddedChannel
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(h_1_inbound, h_2_inbound, h_3_outbound, h_4_outbound);

        // 模拟入站操作
        log.info("inbound:");
        embeddedChannel.writeInbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("hello".getBytes()));

        // 模拟出站操作
        log.info("outbound:");
        embeddedChannel.writeOutbound(ByteBufAllocator.DEFAULT.buffer().writeBytes("out".getBytes()));
    }
}
