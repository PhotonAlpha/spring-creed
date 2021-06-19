package com.ethan.demo.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className: WaitAndNotifyLock
 * @author: Ethan
 * @date: 8/6/2021
 **/
public class AwaitAndSignalLock {
    public static final ReentrantLock lock = new ReentrantLock();
    public static final Condition CONDITION = lock.newCondition();
    public static void main(String[] args) {
        new Thread(() -> {
            try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "\t come ing");
                CONDITION.await();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            System.out.println(Thread.currentThread().getName() + "\t 被唤醒");

        }, "t1").start();
        new Thread(() -> {
            lock.lock();
            try {
                CONDITION.signal();
                System.out.println(Thread.currentThread().getName() + "\t ---通知");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t2").start();
    }
}
