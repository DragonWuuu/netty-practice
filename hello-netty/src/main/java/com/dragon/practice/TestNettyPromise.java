package com.dragon.practice;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Netty Promise
        DefaultPromise<Integer> defaultPromise = new DefaultPromise<>(new NioEventLoopGroup(1).next());

        new Thread(() -> {
            // 模拟执行时间
            try {
                int i = 1 / 0;
                Thread.sleep(1000);
                // 传递成功结果
                defaultPromise.setSuccess(1);
            } catch (Exception e) {
                e.printStackTrace();
                defaultPromise.setFailure(e);
            }
        }).start();
        log.info("{}", defaultPromise.get());

    }
}
