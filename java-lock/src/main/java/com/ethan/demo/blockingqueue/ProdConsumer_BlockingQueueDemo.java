package com.ethan.demo.blockingqueue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @className: ProdConsumer_BlockingQueueDemo
 * @author: Ethan
 * @date: 4/5/2021
 *
 *  旧版参考 {@link SyncAndReentrantLockDemo}
 *
 *      新版
 *      生产------->
 *      消费<-------
 *      生产------->
 *      消费<-------
 *      生产------->
 *      消费<-------
 *
 * volatile/CAS/atomicInteger/BlockQueue/线程交互/原子引用
 *
 **/
public class ProdConsumer_BlockingQueueDemo {
    public static void main(String[] args) {
        MyResource myResource = new MyResource(new ArrayBlockingQueue<>(3));
        new Thread(() -> {
            try {
                System.out.println("生产线程启动成功");
                myResource.myProd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Prod").start();
        new Thread(() -> {
            try {
                System.out.println("消费线程启动成功");
                myResource.myConsumer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Consumer").start();

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("main 线程叫停，活动结束");
            myResource.stop();

        }
    }
}

class MyResource {
    private volatile boolean FLAG = true; // 默认开启，进行生产消费
    private AtomicInteger atomicInteger = new AtomicInteger();
    private BlockingQueue<String> blockingQueue;

    public MyResource(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }
     public void myProd() throws InterruptedException {
         String data = null;
         boolean returnVal;
         while (FLAG) {
             data = "" + atomicInteger.incrementAndGet();
             returnVal = blockingQueue.offer(data, 2, TimeUnit.SECONDS);
             if (returnVal) {
                 System.out.println(Thread.currentThread().getName() + "\t 插入队列 " + data + " 成功");
             } else {
                 System.out.println(Thread.currentThread().getName() + "\t 插入队列 " + data + " 失败");
             }
             TimeUnit.SECONDS.sleep(1);
         }
         System.out.println(Thread.currentThread().getName() + "\t 生产结束");
    }
     public void myConsumer() throws InterruptedException {
         String returnVal;
         while (FLAG) {
             returnVal = blockingQueue.poll( 2, TimeUnit.SECONDS);
             if (returnVal == null || "".equals(returnVal)) {
                 System.out.println(Thread.currentThread().getName() + "\t 消费队列 超过2秒没有获取数据");
                 FLAG = false;
                 return;
             }
             System.out.println(Thread.currentThread().getName() + "\t 消费队列 " + returnVal + " 成功");
         }
    }

    public void stop() {
        this.FLAG = false;
    }

}
