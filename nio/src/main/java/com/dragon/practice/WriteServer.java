package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

@Slf4j
public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey sk = socketChannel.register(selector, 0, null);

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < 30000000; i++) {
                        stringBuilder.append('a');
                    }
                    // 发送超大数据包
                    ByteBuffer buffer = Charset.defaultCharset().encode(stringBuilder.toString());
                    // 监听可写事件
                    sk.interestOps(SelectionKey.OP_WRITE + sk.interestOps());
                    // 附带buffer
                    sk.attach(buffer);
                }
                if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                    int write = socketChannel.write(byteBuffer);
                    log.info("sent: {}", write);

                    // 如果buffer中不存在数据了，说明已经写完了数据，停止writable监听
                    if (!byteBuffer.hasRemaining()) {
                        key.interestOps(key.interestOps() - SelectionKey.OP_WRITE);
                        // 移除附件
                        key.attach(null);
                    }
                }
            }
        }
    }
}
