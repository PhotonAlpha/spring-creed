package com.ethan.demo.lock;

import java.util.concurrent.TimeUnit;

/**
 * @className: WaitAndNotifyLock
 * @author: Ethan
 * @date: 8/6/2021
 **/
public class WaitAndNotifyLock {
    private static final Object objectLock = new Object();
    public static void main(String[] args) {
        new Thread(() -> {
            try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
            synchronized (objectLock) {
                System.out.println(Thread.currentThread().getName() + "\t come ing");
                try {
                    objectLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName() + "\t 被唤醒");
            }
        }, "t1").start();
        new Thread(() -> {
            synchronized (objectLock) {
                objectLock.notify();
                System.out.println(Thread.currentThread().getName() + "\t ---通知");
            }
        }, "t2").start();
    }
}
