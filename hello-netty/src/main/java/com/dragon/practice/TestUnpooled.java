package com.dragon.practice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestUnpooled {
    public static void main(String[] args) {
        ByteBuf byteBuf1 = Unpooled.buffer();
        ByteBuf byteBuf2 = Unpooled.buffer();

        byteBuf1.writeBytes("123".getBytes());
        byteBuf2.writeBytes("456".getBytes());

        // 使用wrappedBuffer零拷贝byteBuf1和ByteBuf2
        ByteBuf byteBuf3 = Unpooled.wrappedBuffer(byteBuf1, byteBuf2);
        printBuffer(byteBuf3);
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
