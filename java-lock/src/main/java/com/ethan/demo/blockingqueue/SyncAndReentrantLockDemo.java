package com.ethan.demo.blockingqueue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className: SyncAndReentrantLockDemo
 * @author: Ethan
 * @date: 3/5/2021
 *      传统
 *              Lock
 *              /  \
 *             /    \
 *            /      \
 *            --------
 *         await      signal
 *
 * 题目： 多线程之间按照顺序调用，实现A -> B -> C三线程启动，要求如下：
 * AA打印5次，BB打印10次，CC打印15次
 * 紧接着
 * AA打印5次，BB打印10次，CC打印15次
 * 。。。
 * 调用10轮
 *
 **/
public class SyncAndReentrantLockDemo {
    public static void main(String[] args) {
        MyShareResource myShareResource = new MyShareResource();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(String.format("****************第%s轮打印print10开始-->", i));
                myShareResource.print10();
            }
        }, "BB").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(String.format("****************第%s轮打印print15开始-->", i));
                myShareResource.print15();
            }
        }, "CC").start();
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(String.format("****************第%s轮打印print5开始-->", i));
                myShareResource.print5();
            }
        }, "AA").start();
    }
}


class MyShareResource {
    private int num = 1;
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();

     public void print5() {
         lock.lock();
         try {
             //防止虚假唤醒，需要加上while
             while (num != 1) {
                 System.out.println("---5-等待唤醒开始");
                 c1.await();
                 System.out.println("---5-等待唤醒结束");
             }
             for (int i = 0; i < 5; i++) {
                 System.out.println(Thread.currentThread().getName() + "\t 开始循环打印:" + (i + 1));
             }
             //修改标记 唤醒2线程
             num = 2;
             // 唤醒线程
             c2.signal();
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             lock.unlock();
         }
     }
     public void print10() {
         lock.lock();
         try {
             //防止虚假唤醒，需要加上while
             while (num != 2) {
                 System.out.println("---10-等待唤醒开始");
                 c2.await();
                 System.out.println("---10-等待唤醒结束");
             }
             for (int i = 0; i < 10; i++) {
                 System.out.println(Thread.currentThread().getName() + "\t 开始循环打印:" + (i + 1));
             }
             //修改标记 唤醒2线程
             num = 3;
             // 唤醒线程
             c3.signal();
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             lock.unlock();
         }
     }
     public void print15() {
         lock.lock();
         try {
             //防止虚假唤醒，需要加上while
             while (num != 3) {
                 System.out.println("---15-等待唤醒开始");
                 c3.await();
                 System.out.println("---15-等待唤醒结束");
             }
             for (int i = 0; i < 15; i++) {
                 System.out.println(Thread.currentThread().getName() + "\t 开始循环打印:" + (i + 1));
             }
             //修改标记 唤醒2线程
             num = 1;
             // 唤醒线程
             c1.signal();
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             lock.unlock();
         }
     }
}