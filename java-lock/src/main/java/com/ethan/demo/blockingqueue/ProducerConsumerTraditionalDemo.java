package com.ethan.demo.blockingqueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className: ProducerConsumerTraditionalDemo
 * @author: Ethan
 * @date: 2/5/2021
 * <p>
 * 初始值为0的变量，两个线程对其交替操作，一个加一个减，循环5次
 * **防止虚假唤醒标志**: 需要 在for循环中 使用#await()
 **/
public class ProducerConsumerTraditionalDemo {
    public static void main(String[] args) {
        ShareData shareData = new ShareData();
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                shareData.increment();
            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                shareData.decrement();
            }
        }, "t2").start();
    }
}

class ShareData {
    private int number = 0;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment() {
        lock.lock();

        try {
            // 1. 判断
            while (number != 0) {
                // 等待
                condition.await();
            }
            // 2. 加值
            number++;
            System.out.println(Thread.currentThread().getName() + "\t 生产了" + number);
            // 3. 通知唤醒
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
    public void decrement() {
        lock.lock();
        try {
            // 1. 判断
            while (number == 0) {
                // 等待
                condition.await();
            }
            // 2. 减值
            number--;
            System.out.println(Thread.currentThread().getName() + "\t 消费了" + number);
            // 3. 通知唤醒
            condition.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
