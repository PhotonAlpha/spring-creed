package com.ethan.demo.lock;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className: WaitAndNotifyLock
 * @author: Ethan
 * @date: 8/6/2021
 **/
public class SignalAllLockDemo {
    public static final ReentrantLock lock = new ReentrantLock();
    public static final Condition reachCondition = lock.newCondition();
    public static void main(String[] args) {


        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"_等待执行信号");
                reachCondition.await();
                System.out.println(Thread.currentThread().getName()+"_copy that, 开始行动");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t1").start();
        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"_等待执行信号");
                reachCondition.await();
                System.out.println(Thread.currentThread().getName()+"_copy that, 开始行动");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t2").start();
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.lock();
            try {
                System.out.println(Thread.currentThread().getName()+"_等待行动指示");
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName()+"_开始行动");
                reachCondition.signalAll();
                // reachCondition.signal();
                System.out.println(Thread.currentThread().getName()+"_任务派发成功");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }, "t3").start();
    }
}
