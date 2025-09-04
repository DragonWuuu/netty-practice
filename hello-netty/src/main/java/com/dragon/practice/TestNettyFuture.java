package com.dragon.practice;

import io.netty.channel.SingleThreadIoEventLoop;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyFuture {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 测试NettyFuture
        // 创建线程池，Netty的线程池是NioEventLoop，继承自ExecutorService
        NioEventLoop nioEventLoop = (NioEventLoop) new NioEventLoopGroup(1).next();

        Future<Integer> integerFuture = nioEventLoop.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Thread.sleep(1000);
                return 0;
            }
        });

        integerFuture.addListener(future -> {
            log.info("listener...{}", future.get());
        });
        integerFuture.sync();
        log.info("{}", integerFuture.get());

    }
}
