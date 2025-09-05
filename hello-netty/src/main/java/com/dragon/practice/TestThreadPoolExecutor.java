package com.dragon.practice;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TestThreadPoolExecutor {
    public static void main(String[] args) {
        // 1. 配置参数
        int corePoolSize = 2; // 核心线程数，如CPU核心数
        int maximumPoolSize = 5; // 最大线程数，根据峰值流量设定
        long keepAliveTime = 60L; // 空闲线程存活时间
        TimeUnit unit = TimeUnit.SECONDS; // 时间单位
        int queueCapacity = 3; // 队列容量

        // 2. 创建有界工作队列
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(queueCapacity);

        // 3. 使用自定义的线程工厂
        ThreadFactory threadFactory = new ThreadFactory() {
            AtomicInteger integer = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "custom-pool-" + integer.getAndIncrement());
                return thread;
            }
        };

        // 4. 选择拒绝策略
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        // 5. 手动创建线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler // 通常放最后，避免参数错位
        );

        // 6. 提交任务 (最好用 try-catch 捕获 RejectedExecutionException)
        try {
            for (int i = 0; i < 10; i++) {
                final int taskId = i;
                executor.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + " 正在执行任务 " + taskId);
                    try {
                        Thread.sleep(1000); // 模拟任务耗时
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        } catch (RejectedExecutionException e) {
            System.err.println("任务被拒绝执行了: " + e.getMessage());
            // 在这里处理任务提交失败的情况，如记录日志、重试、存入数据库等
        } finally {
            // 7. 优雅关闭线程池（非常重要！）
            executor.shutdown();
            try {
                // 等待所有任务完成，或者超时后强制关闭
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
