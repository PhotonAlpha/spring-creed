/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.demo.atomic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.LongAdder;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/9/2022 5:34 PM
 */
public class LongAdderDemo {
    public static void main(String[] args) throws InterruptedException {
        LongAdder counter = new LongAdder();
        ExecutorService service = Executors.newFixedThreadPool(16);
        for (int i = 0; i < 100; i++) {
            service.submit(new Task(counter));
        }
        Thread.sleep(2000);
        System.out.println(counter.sum());
        service.shutdown();
    }

    static class Task implements Runnable {
        private final LongAdder counter;

        public Task(LongAdder counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            counter.increment();
        }
    }
}
