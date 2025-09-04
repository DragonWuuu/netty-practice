package com.dragon.practice;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class testJDKFuture {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        // 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 创建任务,使用`submit(Callable<T>)`可以使用Future拿到结果，使用`execute(Runnable)`和`submit(Runnable)`拿不到返回结果。
        Future<Integer> integerFuture = executorService.submit(new Callable<Integer>() {
            @Override
            public Integer call() {
                // 模拟执行时间
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return 0;
            }
        });
        // 阻塞获取结果
        Integer i = integerFuture.get();
        log.info("{}", i);

    }
}
