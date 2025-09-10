package com.dragon.practice;

import com.dragon.practice.message.Message;
import com.dragon.practice.message.MessageBuilder;
import com.dragon.practice.message.MessageType;
import com.dragon.practice.protocol.MessageCodec;
import com.dragon.practice.protocol.MessageCodecSharable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmbeddedTest {
    public static void main(String[] args) throws Exception {
        LoggingHandler loggingHandler = new LoggingHandler();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                loggingHandler,
                new LengthFieldBasedFrameDecoder(1024 * 1024, 12, 4, 0, 0),
                messageCodecSharable
        );
        Message message = MessageBuilder.builder()
                .setData("hello Message")
                .setType(MessageType.LOGIN)
                .setSequence(1)
                .build();
        log.info("{}",message.getClass());
        // 测试发送消息
        embeddedChannel.writeOutbound(message);
        // 接收消息
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, byteBuf);
        embeddedChannel.writeInbound(byteBuf);

    }
}
