package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Main {
    private static void split(ByteBuffer source){
        source.flip();
        for(int i=0; i < source.limit(); i++){
            if(source.get(i) == '\n'){
                // 找到了一条完整消息
                int length = i + 1 - source.position();
                // 把这条消息写入新的ByteBuffer
                ByteBuffer target = ByteBuffer.allocate(length);
                // 从source读，向target写
                for (int j = 0; j < length; j++){
                    target.put(source.get());
                }
                target.flip();
                log.info("{}", Charset.defaultCharset().decode(target));
            }
        }
        // 将剩余的压缩
        source.compact();
    }
    public static void main(String[] args) {
        // 使用selector优化单线程非阻塞时，线程不断轮询的问题。使用标记分隔符处理消息边界问题
        try {
            // 打开selector
            Selector selector = Selector.open();
            // 使用NIO创建服务器
            ServerSocketChannel  serverSocketChannel = ServerSocketChannel.open();
            log.info("opening serverSocketChannel");
            // 绑定监听端口
            serverSocketChannel.bind(new InetSocketAddress(8080));
            log.info("bing port 8080");
            // 配置非阻塞
            serverSocketChannel.configureBlocking(false);
            // 注册selector，监听accept事件，sscKey就是事件发生后，通过它可以知道事件和哪个channel的事件
            SelectionKey sscKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, null);

            do {
                log.info("waiting for events...");
                // 阻塞，等待事件发生
                selector.select();
                /* 处理事件
                * ！注意selectedKeys是一个标记空间，它保存了触发事件的key，
                * 但在我们处理完事件后，它不会主动地从selectedKeys中删除key，所以需要我们主动删除。
                 */
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    log.info("selected key: {}", key);
                    if (key.isAcceptable()) {
                        SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();
                        socketChannel.configureBlocking(false);
                        // ByteBuffer
                        ByteBuffer buffer = ByteBuffer.allocate(10);
                        // 监听Read事件，attach buffer
                        socketChannel.register(selector, SelectionKey.OP_READ, buffer);
                    }
                    if (key.isReadable()) {
                        try {
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            // 获取(ByteBuffer)attachment
                            ByteBuffer byteBuffer = (ByteBuffer) key.attachment();
                            int read = socketChannel.read(byteBuffer);
                            // 当客户端正常断开时，会触发read事件，read的返回值为-1，这时候需要将连接客户端的channel删除，即key.cancel()删除注册信息
                            if (read == -1){
                                log.info("client close: {}", socketChannel);
                                key.cancel();
                                continue;
                            }
                            // 按照规则分割并打印
                            split(byteBuffer);
                            // 如果byteBuffer还有剩余未处理数据，表示这些数据还不完整，为了给完整数据留足空间，需要扩容ByteBuffer
                            if (byteBuffer.position() == byteBuffer.limit()) {
                                byteBuffer.flip();
                                ByteBuffer extendBuffer = ByteBuffer.allocate(byteBuffer.limit()*2);
                                extendBuffer.put(byteBuffer);
                                // 将新的extendBuffer attach to key
                                key.attach(extendBuffer);
                            }
                        // 当客户端不正常断开时，会触发io异常，这时候需要将channel删除，即key.cancel()删除注册信息
                        } catch (IOException e) {
                            log.error(e.getMessage());
                            key.cancel();
                        }
                    }

                    // 遇到不想处理的事件可以取消,否则这个事件会一直触发select(),变成了类似非阻塞的状态
//                    key.cancel();
                    // 处理完事件了，从SelectorKeys中删除处理了的key
                    iterator.remove();
                }

            } while (true);

        } catch (IOException e) {
            log.error("error", e);
        }
    }
}