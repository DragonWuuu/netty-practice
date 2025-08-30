package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        // 使用NIO理解阻塞模式

        // buffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        try {
            // 使用NIO创建服务器
            ServerSocketChannel  serverSocketChannel = ServerSocketChannel.open();
            log.info("opening serverSocketChannel");
            // 配置非阻塞
            serverSocketChannel.configureBlocking(false);
            // 绑定监听端口
            serverSocketChannel.bind(new InetSocketAddress(8080));
            log.info("bing port 8080");
            // 建立多客户端的连接
            List<SocketChannel> channels = new ArrayList<>();
            do {
                SocketChannel channel = serverSocketChannel.accept();
                if (channel != null) {
                    channels.add(channel);
                    log.info("accept client: {}", channel);
                    // 配置非阻塞
                    channel.configureBlocking(false);
                }
                // 读数据
                for (SocketChannel socketChannel : channels) {
                    // 非阻塞模式，没有数据可读，会返回0
                    int read = socketChannel.read(buffer);
                    if (read > 0) {
                        buffer.flip();
                        while (buffer.hasRemaining()) {
                            log.info("{}", (char) buffer.get());
                        }
                        buffer.clear();
                    }
                }
            } while (true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}