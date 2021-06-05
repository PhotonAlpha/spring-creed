package com.ethan.demo.lock;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @className: CycliBarrierDemo
 * @author: Ethan
 * @date: 2/5/2021
 * 到齐了开始执行
 **/
public class CyclicBarrierDemo {
    public static final CyclicBarrier CYCLIC_BARRIER = new CyclicBarrier(7, () -> {
        System.out.println(Thread.currentThread().getName()+"\t 召唤神龙");
    });
    public static void main(String[] args) {
        for (int i = 1; i <= 7; i++) {
            final int index = i;
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + "\t 收集到第" + index + "龙珠");
                try {
                    CYCLIC_BARRIER.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, "t" + i).start();
        }
    }
}
