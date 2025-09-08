package com.dragon.practice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestCompositeByteBuf {
    public static void main(String[] args) {
        // 不使用CompositeByteBuf拼接ByteBuf
        ByteBuf byteBuf1 = ByteBufAllocator.DEFAULT.buffer();
        byteBuf1.writeBytes("1234".getBytes());
        ByteBuf byteBuf2 = ByteBufAllocator.DEFAULT.buffer();
        byteBuf2.writeBytes("5678".getBytes());

        // 内存发生了拷贝
        ByteBuf byteBuf3 = ByteBufAllocator.DEFAULT.buffer();
//        byteBuf3.writeBytes(byteBuf1).writeBytes(byteBuf2);

        // 使用CompositeByteBuf, 观察读写指针
        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        byteBuf1.retain();
        byteBuf2.retain();
        compositeByteBuf.addComponents(true, byteBuf1, byteBuf2);
        printBuffer(compositeByteBuf);

        byteBuf1.release();
        // 功能正常使用
        printBuffer(compositeByteBuf);
        // 使用完后记得释放
        byteBuf1.release();
        byteBuf2.release();


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
