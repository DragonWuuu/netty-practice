package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");

        log.info("boss started port: 8080");
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));
        Selector boss = Selector.open();
        serverSocketChannel.register(boss, SelectionKey.OP_ACCEPT);

        // 创建workers
        Worker[] workers = new Worker[10];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new Worker("worker-" + i, 16);
            workers[i].start();
        }
        AtomicInteger atomicInteger = new AtomicInteger(0);
        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel channel = ((ServerSocketChannel) key.channel()).accept();
                    channel.configureBlocking(false);

                    log.info("accept: {}", channel);
                    // 向worker register new channel
                    // round robin
                    workers[atomicInteger.getAndIncrement() % workers.length].registerReadable(channel);
                }
            }
        }
    }
    // readable handler
    static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private String name;
        private int bufferSize;
        ConcurrentLinkedQueue<Runnable> tasks = new ConcurrentLinkedQueue<>();
        private volatile boolean started = false;

        public Worker(String name, int bufferSize) {
            this.name = name;
            this.bufferSize = bufferSize;
        }

        public void start() {
            if (this.started) {
                return; // 快速判断
            }
            synchronized (this){ // 线程安全，防止重复启动
                if (!started) {
                    try {
                        this.thread = new Thread(this, name);
                        this.selector = Selector.open();
                        // 将started置为true不要放在Thread.start()和Selector.open()方法前面，
                        // 避免它们抛出异常导致虚假启动。
                        started = true;
                        this.thread.start();
                        log.info("worker started name: {}", name);
                    } catch (IOException e) {
                        log.error("{}", e);
                    }
                }
            }
        }

        public void registerReadable(SocketChannel socketChannel) {
            this.start();
            // 将注册任务放入队列
            this.tasks.add(() -> {
                try {
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(this.bufferSize));
                    // 关联worker的selector
                    log.info("registered worker: {}", socketChannel);
                } catch (ClosedChannelException e) {
                    log.error("{}", e);
                }
            });
            this.selector.wakeup(); // 唤醒被selected()阻塞着的selector
        }
        @Override
        public void run() {
            while (true) {
                try {
                    log.info("wait for event... name:{}", this.name);
                    this.selector.select();
                    // 如果任务队列有任务需要执行，则执行任务。
                    while (!this.tasks.isEmpty()) {
                        this.tasks.poll().run();
                    }
                    Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ((ByteBuffer) key.attachment());
                            int read = channel.read(buffer);
                            if (read == -1) {
                                log.info("worker: {}, client closed: {}", this.name, channel);
                                key.cancel();
                                continue;
                            }
                            buffer.flip();
                            log.info("worker: {}, read: {}", this.name, Charset.defaultCharset().decode(buffer));
                            buffer.clear();
                        }
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
