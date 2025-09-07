package com.dragon.practice;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

public class TestDuplicate {
    public static void main(String[] args) {
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer(10);
        byteBuf.writeBytes("12345412345".getBytes());
        System.out.println("============byteBuf=============");
        printBuffer(byteBuf);

        // 使用duplicate获取byteBuf的所有内容，观察capacity
        ByteBuf duplicate = byteBuf.duplicate();
        System.out.println("============duplicate=============");
        printBuffer(duplicate);
        // 当前duplicate的capacity是32，写入数个a，使capacity需求超过32位，观察ByteBuf的容量变化
        duplicate.writeBytes("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes());
        System.out.println("============byteBuf=============");
        printBuffer(byteBuf);
        System.out.println("============duplicate=============");
        printBuffer(duplicate);
        // 对原始ByteBuf写入数据，观察duplicate的变化
        byteBuf.writeBytes("789".getBytes());
        System.out.println("============byteBuf=============");
        printBuffer(byteBuf);
        System.out.println("============duplicate=============");
        printBuffer(duplicate);
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
