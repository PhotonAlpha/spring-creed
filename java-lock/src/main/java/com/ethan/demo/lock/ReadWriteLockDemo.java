package com.ethan.demo.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @className: ReadWriteLockDemo
 * @author: Ethan
 * @date: 2/5/2021
 **/
public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCache myCache = new MyCache();
        for (int i = 1; i < 6; i++) {
            final int index = i;
            new Thread(() -> {
                myCache.put("key" + index, index);
            }, "writeT" + i).start();
        }
        for (int i = 1; i < 6; i++) {
            final int index = i;
            new Thread(() -> {
                myCache.get("key" + index);
            }, "readT" + i).start();
        }

    }
}


class MyCache {
    private volatile Map<String, Object> map = new HashMap<>();
    //读写锁
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void put(String key, Object value) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t 正在写入:" + key);
            TimeUnit.SECONDS.sleep(1);
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "\t 写入完成");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void get(String key) {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t 正在读取:" + key);
            TimeUnit.SECONDS.sleep(1);
            Object value = map.get(key);
            System.out.println(Thread.currentThread().getName() + "\t 读取完成:" + value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
    }
}
