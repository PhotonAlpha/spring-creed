package com.ethan.demo.lock;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className: WaitAndNotifyLock
 * @author: Ethan
 * @date: 8/6/2021
 **/
public class LockSupportLock {
    public static void main(String[] args) {
        // ReentrantLock lock = new ReentrantLock();
        // lock.lock();

        // ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(10, 20, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));
        // threadPoolExecutor.execute();
        // threadPoolExecutor.submit()
        // DefaultSingletonBeanRegistry

        // Thread t1 = new Thread(() -> {
        //     try {TimeUnit.SECONDS.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
        //     System.out.println(Thread.currentThread().getName() + "\t come ing");
        //     LockSupport.park();// 被阻社，等待通知等待唤醒，需要许可证
        //     LockSupport.park();// 被阻社，等待通知等待唤醒，需要许可证
        //     System.out.println(Thread.currentThread().getName() + "\t 被唤醒");
        //
        // }, "t1");
        // t1.start();
        // Thread t2 = new Thread(() -> {
        //     LockSupport.unpark(t1);
        //     System.out.println(Thread.currentThread().getName() + "\t ---通知");
        // }, "t2");
        // t2.start();
    }

    class BeanFactoryProcessor implements BeanPostProcessor {

    }

}
