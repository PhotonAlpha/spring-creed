package com.ethan.demo.lock;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @className: SemaphoreDemo
 * @author: Ethan
 * @date: 2/5/2021
 *
 * 抢车位
 **/
public class SemaphoreDemo {
    public static final Semaphore SEMAPHORE = new Semaphore(3); //模拟3个车位

    public static void main(String[] args) {
        SEMAPHORE.release();
        SEMAPHORE.release();
        SEMAPHORE.release();
        for (int i = 1; i <= 6; i++) {//6辆汽车
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + "\t 开始抢车位");
                    boolean acquired = SEMAPHORE.tryAcquire(2, TimeUnit.SECONDS);
                    System.out.println(Thread.currentThread().getName() + "\t 抢到车位:" + acquired);
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(Thread.currentThread().getName() + "\t 离开车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("availablePermits:"+SEMAPHORE.availablePermits());
                    // int a = 1 / 0;
                    SEMAPHORE.release();
                }
            }, "t" + i).start();

        }
        System.out.println("==end");
    }
}
