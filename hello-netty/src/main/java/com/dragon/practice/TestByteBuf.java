package com.dragon.practice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

@Slf4j
public class TestByteBuf {
    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(12);

        printBuffer(byteBuf);

        // 写入8字节数据
        byteBuf.writeLong(10L);

        printBuffer(byteBuf);

        // 读取两字节
        byteBuf.readBytes(2);

        printBuffer(byteBuf);

        // 使用mark标记读指针的坐标，以备还原读指针
        byteBuf.markReaderIndex();
        // 读取两字节数据，读指针向前运动两个字节。
        byteBuf.readBytes(2);
        // 还原读指针
        byteBuf.resetReaderIndex();

        printBuffer(byteBuf);
        byteBuf.release();

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
