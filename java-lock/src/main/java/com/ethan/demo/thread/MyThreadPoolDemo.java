package com.ethan.demo.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @className: MyThreadPoolDemo
 * @author: Ethan
 * @date: 9/5/2021
 **/
public class MyThreadPoolDemo {
    public static void main(String[] args) {
        // Executors.newScheduledThreadPool(5);
        // Executors.newFixedThreadPool(5);
        // Executors.newSingleThreadExecutor();
        // Executors.newCachedThreadPool();
        System.out.println(Runtime.getRuntime().availableProcessors());

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
                5,
                1, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardPolicy());

        try {
            for (int i = 0; i < 16; i++) {
                final int index =i;
                threadPoolExecutor.execute(() -> {
                    System.out.println(Thread.currentThread().getName() + "\t 办理业务成功:" + index);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadPoolExecutor.shutdown();
        }

    }
}
