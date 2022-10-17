package com.ethan.demo.lock;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * @className: CycliBarrierDemo
 * @author: Ethan
 * @date: 2/5/2021
 * 到齐了开始执行
 **/
public class CyclicBarrier2Demo {
    public static final CyclicBarrier CYCLIC_BARRIER = new CyclicBarrier(3, () -> {
        System.out.println(String.format("【线程%s发车】", Thread.currentThread().getName()));
    });

    public static void main(String[] args) {
        Random random = new Random();
        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                try {
                    int nextInt = random.nextInt(10);
                    System.out.println(String.format("线程%s即将到达集合地点1，休息%ss", Thread.currentThread().getName(),
                            nextInt));
                    TimeUnit.SECONDS.sleep(nextInt);

                    int waiting = CYCLIC_BARRIER.getNumberWaiting();
                    String output = waiting == 2 ? "都到齐了，继续走啊" : "正在等候";
                    System.out.println(String.format("[线程%s]已达集合地点1当前已有%s个已经到达，%s", Thread.currentThread().getName(),
                            waiting, output));
                    CYCLIC_BARRIER.await();

                    nextInt = 3;
                    System.out.println(String.format("线程%s即将到达集合地点2，休息%ss", Thread.currentThread().getName(),
                            nextInt));
                    TimeUnit.SECONDS.sleep(nextInt);
                    int waiting2 = CYCLIC_BARRIER.getNumberWaiting();
                    String output2 = waiting2 == 2 ? "都到齐了，继续走啊" : "正在等候";
                    System.out.println(String.format("[线程%s]已达集合地点2当前已有%s个已经到达，%s", Thread.currentThread().getName(),
                            waiting, output2));
                    CYCLIC_BARRIER.await();

                    nextInt = random.nextInt(10);
                    System.out.println(String.format("线程%s即将到达集合地点3，休息%ss", Thread.currentThread().getName(),
                            nextInt));
                    TimeUnit.SECONDS.sleep(nextInt);
                    int waiting3 = CYCLIC_BARRIER.getNumberWaiting();
                    String output3 = waiting3 == 2 ? "都到齐了，继续走啊" : "正在等候";
                    System.out.println(String.format("[线程%s]已达集合地点3当前已有%s个已经到达，%s", Thread.currentThread().getName(),
                            waiting3, output3));
                    CYCLIC_BARRIER.await();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, "t" + i).start();
        }


    }
}
