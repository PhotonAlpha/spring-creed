package com.ethan.demo.lock;


import org.springframework.util.StopWatch;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @className: CountDownLatchDemo
 * @author: Ethan
 * @date: 2/5/2021
 **/
public class CountDownLatchDemo {
    public static final CountDownLatch countDownLatch = new CountDownLatch(6);

    public static void main(String[] args) {
        Random random = new Random();
        StopWatch watch = new StopWatch();
        watch.start("moniter");
        for (int i = 1; i < 7; i++) {
            new Thread(() -> {
                int nextInt = random.nextInt(10);
                System.out.println(Thread.currentThread().getName() + "\t 上完自习，"+nextInt+"s 后离开教室");
                try {
                    TimeUnit.SECONDS.sleep(nextInt);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                countDownLatch.countDown();
            }, "t" + i).start();
        }
        try {
            countDownLatch.await();
            watch.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "\t 班长最后关门走人" + watch.prettyPrint());
    }
}
