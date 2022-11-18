package com.ethan.demo.blockingqueue;

import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * @className: BlockingQueueDemo
 * @author: Ethan
 * @date: 2/5/2021
 **/
public class BlockingQueueDemo {
    public static void main(String[] args) {
        // testArrayBlockingQueue();

        testSynchronousQueue();
    }
    public static void testSynchronousQueue() {
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "\t put1");
                synchronousQueue.put("1");
                System.out.println(Thread.currentThread().getName() + "\t put2");
                synchronousQueue.put("2");
                System.out.println(Thread.currentThread().getName() + "\t put3");
                synchronousQueue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();

        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + "\t take1");
                System.out.println(synchronousQueue.take());
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + "\t take2");
                System.out.println(synchronousQueue.take());
                TimeUnit.SECONDS.sleep(5);
                System.out.println(Thread.currentThread().getName() + "\t take3");
                System.out.println(synchronousQueue.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }


    public static void testArrayBlockingQueue() {
        ArrayBlockingQueue<String> blockingQueue = new ArrayBlockingQueue<>(3);
        try {
            blockingQueue.put("a");
            blockingQueue.put("b");
            blockingQueue.put("c");
            //阻塞
            // blockingQueue.put("d");
            System.out.println("===========");

            blockingQueue.take();
            blockingQueue.take();
            blockingQueue.take();
            //阻塞
            blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        try {
            StopWatch stopWatch = new StopWatch("CrunchifyThreads");
            stopWatch.start();
            System.out.println(blockingQueue.offer("a", 2, TimeUnit.SECONDS));
            stopWatch.stop();

            stopWatch.start();
            System.out.println(blockingQueue.offer("b", 2, TimeUnit.SECONDS));
            stopWatch.stop();

            stopWatch.start();
            System.out.println(blockingQueue.offer("c", 2, TimeUnit.SECONDS));
            stopWatch.stop();

            /**
             *  如果此时队列已满，会一直阻塞在这里，直到队列不满或者三秒超时返回false
             */
            stopWatch.start();
            System.out.println(blockingQueue.offer("d", 2, TimeUnit.SECONDS));
            stopWatch.stop();

            stopWatch.start();
            System.out.println(blockingQueue.offer("e", 2, TimeUnit.SECONDS));
            stopWatch.stop();

            stopWatch.start("poll1");
            System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
            stopWatch.stop();
            stopWatch.start("poll2");
            System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
            stopWatch.stop();
            stopWatch.start("poll3");
            System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
            stopWatch.stop();
            /**
             *  如果此时队列已满，会一直阻塞在这里，直到队列不满或者三秒超时返回false
             */
            stopWatch.start("poll4");
            System.out.println(blockingQueue.poll(2, TimeUnit.SECONDS));
            stopWatch.stop();

            System.out.println("Total time:"+stopWatch.getTotalTimeSeconds());
            System.out.println(stopWatch.prettyPrint());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        blockingQueue.offer("abc");//添加
        blockingQueue.peek();//检查
        blockingQueue.poll();//提取

/*        // 添加元素
        System.out.println(blockingQueue.add("a"));
        System.out.println(blockingQueue.add("b"));
        System.out.println(blockingQueue.add("c"));
        System.out.println(blockingQueue.add("d"))
        // 检查元素
        System.out.println(blockingQueue.element());
        // 删除元素
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());
        System.out.println(blockingQueue.remove());*/
    }
}
