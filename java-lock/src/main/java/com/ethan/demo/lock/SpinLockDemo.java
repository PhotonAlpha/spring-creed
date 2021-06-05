package com.ethan.demo.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @className: SpinLockDemo
 * @author: Ethan
 * @date: 2/5/2021
 * 实现一个自旋锁
 * <p>
 * 通过CAS操作完成自旋，当前线程持有锁，不是null，所以只能通过自旋等待，直到A释放锁后B随后抢到。
 **/
public class SpinLockDemo {

    // 原子引用线程
    AtomicReference<Thread> atomicReference = new AtomicReference();


    public void myLock() {
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName() + "\t myLock come in...");
        while (!atomicReference.compareAndSet(null, thread)) {

        }
    }
    public void myUnLock() {
        Thread thread = Thread.currentThread();
        System.out.println(thread.getName() + "\t myUnLock come in...");
        atomicReference.compareAndSet(thread, null);
    }


    public static void main(String[] args) {
        SpinLockDemo spinLockDemo = new SpinLockDemo();
        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                spinLockDemo.myLock();
                try {
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println(Thread.currentThread().getName() + "finished");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    spinLockDemo.myUnLock();
                }
            }, "t" + i).start();
        }
    }


}
