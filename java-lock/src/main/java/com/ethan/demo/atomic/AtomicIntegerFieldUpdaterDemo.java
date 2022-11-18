/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.demo.atomic;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/9/2022 5:06 PM
 */
public class AtomicIntegerFieldUpdaterDemo implements Runnable {
    static Score math;
    static Score computer;
    public static AtomicIntegerFieldUpdater<Score> scoreUpdater = AtomicIntegerFieldUpdater
            .newUpdater(Score.class, "score");

    public static class Score {
        volatile int score;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            computer.score++;
            scoreUpdater.getAndIncrement(math);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        math =new Score();
        computer =new Score();
        AtomicIntegerFieldUpdaterDemo r = new AtomicIntegerFieldUpdaterDemo();
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println("普通变量的结果："+ computer.score);
        System.out.println("升级后的结果："+ math.score);
    }
}
