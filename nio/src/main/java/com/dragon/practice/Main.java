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
        try {
            // 获取文件输入流
            FileInputStream fis = new FileInputStream("nio/src/main/resources/test.txt");
            // 获取channel
            FileChannel channel = fis.getChannel();
            // 分配缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            do {
                // 从channel中读取数据到缓冲区
                int bytesRead = channel.read(buffer);
                if (bytesRead == -1) {break;}
                // 切换缓冲区为读取模式
                buffer.flip();
                // 从缓冲区中读取数据
                while (buffer.hasRemaining()) {
                    byte b = buffer.get();
                    log.info("{}", (char) b);
                }
                // 清空缓冲区
                buffer.clear();
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}