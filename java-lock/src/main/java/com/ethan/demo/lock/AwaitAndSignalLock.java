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
public class AwaitAndSignalLock {
    static class NumberWrapper {
        public int value = 1;
    }
    public static final ReentrantLock lock = new ReentrantLock();
    //第一个条件当屏幕上输出到3
    public static final Condition reachThreeCondition = lock.newCondition();
    //第二个条件当屏幕上输出到6
    public static final Condition reachSixCondition = lock.newCondition();
    public static void main(String[] args) {
        Callable<String> callable = new Callable<>(){
            @Override
            public String call() throws Exception {
                return "null";
            }
        };
        Executors.newFixedThreadPool(1).submit(callable);
        //NumberWrapper只是为了封装一个数字，一边可以将数字对象共享，并可以设置为final
        //注意这里不要用Integer, Integer 是不可变对象
        final NumberWrapper num = new NumberWrapper();
        //初始化A线程
        Thread threadA = new Thread(() -> {
            //需要先获得锁
            lock.lock();
            System.out.println("threadA get lock1");
            try {
                System.out.println("threadA start write");
                //A线程先输出前3个数
                while (num.value <= 3) {
                    System.out.println(num.value);
                    num.value++;
                    TimeUnit.SECONDS.sleep(1);
                }
                //输出到3时要signal，告诉B线程可以开始了
                reachThreeCondition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("threadA release lock");
                lock.unlock();
            }
            lock.lock();
            System.out.println("threadA get lock2");
            try {
                //等待输出6的条件
                reachSixCondition.await();
                System.out.println("threadA start write");
                //输出剩余数字
                while (num.value <= 9) {
                    System.out.println(num.value);
                    num.value++;
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("threadA release lock");
                lock.unlock();
            }
        });


        Thread threadB = new Thread(() -> {
            try {
                lock.lock();
                System.out.println("threadB get lock1");
                while (num.value <= 3) {
                    //等待3输出完毕的信号
                    reachThreeCondition.await();
                    System.out.println("threadB:" + num.value);
                    TimeUnit.SECONDS.sleep(1);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("threadB release lock");
                lock.unlock();
            }
            try {
                lock.lock();
                System.out.println("threadB get lock2");
                //已经收到信号，开始输出4，5，6
                System.out.println("threadB start write");
                while (num.value <= 6) {
                    System.out.println(num.value);
                    num.value++;
                    TimeUnit.SECONDS.sleep(1);
                }
                //4，5，6输出完毕，告诉A线程6输出完了
                reachSixCondition.signal();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("threadB release lock");
                lock.unlock();
            }
        });


        //启动两个线程
        threadB.start();
        threadA.start();
    }
}
