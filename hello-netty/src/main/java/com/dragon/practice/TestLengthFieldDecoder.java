package com.dragon.practice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;

@Slf4j
public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(100, 1, 2, 2, 5),
                new LoggingHandler()
        );

        // 约定2个字节长度
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        makeMessage("hello, world", buffer);
        makeMessage("hi!", buffer);
        makeMessage("12345", buffer);
        makeMessage("你好", buffer);
        embeddedChannel.writeInbound(buffer);


    }

    private static void makeMessage(String sendString, ByteBuf byteBuf) {
        // 计算长度，用UTF_8避免因为中文长度计算错误。
        int length = StandardCharsets.UTF_8.encode(sendString).limit();
        // 在长度前添加额外字段需要修改lengthFieldOffset的值
        byteBuf.writeByte(61);
        // 根据lengthFieldLength指定的字节数装填长度
        byteBuf.writeShort(length);
        // 在长度后添加额外字段需要修改lengthAdjustment的值
        byteBuf.writeByte(62);
        byteBuf.writeByte(63);
        // 写入长度为length的数据
        byteBuf.writeBytes(sendString.getBytes());
    }
}
