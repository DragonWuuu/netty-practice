package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
public class Client {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 8080));
        log.info("Connected to server {}", socketChannel.getRemoteAddress());
        socketChannel.write(ByteBuffer.wrap("Hello2".getBytes()));
        socketChannel.close();

    }
}
