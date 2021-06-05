package com.ethan.demo.lock;

import java.util.concurrent.CountDownLatch;

/**
 * @className: CountDownLatchDemo
 * @author: Ethan
 * @date: 2/5/2021
 **/
public class CountDownLatchDemo {
    public static final CountDownLatch countDownLatch = new CountDownLatch(6);

    public static void main(String[] args) {
        for (int i = 1; i < 7; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 上完自习，离开教室");
                countDownLatch.countDown();
            }, "t" + i).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "\t 班长最后关门走人");
    }
}
