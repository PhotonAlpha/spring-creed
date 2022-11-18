/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/16/2022 3:57 PM
 */
public class LockTest {
    final static ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
    static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("mm:ss");
    public static ExecutorService threadPool = Executors.newFixedThreadPool(16);

    public static void main(String args[]) throws InterruptedException {

        // PrintQueue printQueue = new PrintQueue();
        // Thread thread[] = new Thread[10];
        // for (int i = 0; i < 10; i++) {
        //     thread[i] = new Thread(new Job(printQueue), "Thread " + i);
        // }
        // for (int i = 0; i < 10; i++) {
        //     thread[i].start();
        //     try {
        //         Thread.sleep(100);
        //     } catch (InterruptedException e) {
        //         e.printStackTrace();
        //     }
        // }


        for (int i = 0; i < 1000; i++) {
            int finalI = i;
            threadPool.submit(() -> {
                String date = new LockTest().date(finalI);
                System.out.println(date);
            });
        }

        threadPool.shutdown();
    }

    public String date(int seconds) {
        Date date = new Date(1000 * seconds);
        // LocalDateTime dt = LocalDateTime.from(date.toInstant().atZone(ZoneId.systemDefault()));
        // return dtf.format(dt);
        return ThreadSafeFormatter.dateFormatThreadLocal.get().format(date);
    }
}

class ThreadSafeFormatter {
    public static ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = new ThreadLocal<>() {
        @Override
        protected SimpleDateFormat initialValue() {
            // return LockTest.dateFormat;// 有线程安全问题
            return new SimpleDateFormat("mm:ss");
        }
    };
}



