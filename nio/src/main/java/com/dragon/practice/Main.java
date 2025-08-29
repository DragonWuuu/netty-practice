package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class Main {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        buffer.put((byte) 0x62);
        // 在不flip的情况下读数据：
//        log.info("在不flip的情况下读数据：{}", (char) buffer.get());
        // 反转后读数据
        buffer.flip();
        log.info("反转后读数据：{}", (char) buffer.get());
        // 压缩后
        buffer.compact();
        // 此时读数据看看是不是读到了没被删除的0x62
        log.info("查看position：{}", buffer.position());
        log.info("压缩后读数据：{}", (char) buffer.get());
        log.info("查看position：{}", buffer.position());
        log.info("使用get(1)方法读取数据：{}", (char) buffer.get(1));
        log.info("查看position：{}", buffer.position());
        log.info("总结：谨慎使用get()方法，应当在调用flip()方法后使用，但使用get(1)方法不会移动指针");
    }
}