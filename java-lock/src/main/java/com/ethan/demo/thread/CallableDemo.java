package com.ethan.demo.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @className: CallableDemo
 * @author: Ethan
 * @date: 9/5/2021
 **/
public class CallableDemo {
    public static void main(String[] args) throws ExecutionException {
        FutureTask<Integer> task = new FutureTask<>(new MyThread2());
        new Thread(task, "t1").start();

        new Thread(new MyThread(), "t1").start();

        while (!task.isDone()) {
            System.out.println("check----");
        }

        try {
            Integer integer = task.get();
            System.out.println("integer" + integer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("---------");
    }
}

class MyThread implements Runnable {
    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " \t MyThread running");
    }
}

class MyThread2 implements Callable<Integer> {
    @Override
    public Integer call() throws Exception {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " \t MyThread2 running");
        return 111;
    }
}