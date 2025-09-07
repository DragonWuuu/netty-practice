package com.dragon.practice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestSlice {
    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(10);
        byteBuf.writeBytes("12345412345".getBytes());
        System.out.println("============byteBuf=============");
        printBuffer(byteBuf);
        // 截取从下标3开始，长度为4的byteBuf
        ByteBuf slice = byteBuf.slice(3, 4);
        printBuffer(slice);

        // 对slice做修改，观察byteBuf的变化
        slice.setBytes(0, "abc".getBytes());
        printBuffer(byteBuf);
        printBuffer(slice);
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
