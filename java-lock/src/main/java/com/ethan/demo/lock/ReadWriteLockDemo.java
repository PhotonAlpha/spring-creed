package com.ethan.demo.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private static final Logger log = LoggerFactory.getLogger(MyCache.class);
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

/**
 * 插队策略
 * 公平策略下，只要队列里有线程已经在排队，就不允许插队。
 * 非公平策略下：
 *  - 如果允许读锁插队，那么由于读锁可以同时被多个线程持有，所以可能造成源源不断的后面的线程一直插队成功，导致读锁一直不能完全释放，从而导致写锁一直等待，为了防止“饥饿”，在等待队列的头结点是尝试获取写锁的线程的时候，不允许读锁插队。
 *  - 写锁可以随时插队，因为写锁并不容易插队成功，写锁只有在当前没有任何其他线程持有读锁和写锁的时候，才能插队成功，同时写锁一旦插队失败就会进入等待队列，所以很难造成“饥饿”的情况，允许写锁插队是为了提高效率。
 *
 * 升降级策略：只能从写锁降级为读锁，不能从读锁升级为写锁。
 */
class ReadLockJumpQueue {
    private static final Logger log = LoggerFactory.getLogger(ReadLockJumpQueue.class);
    private static final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock
            .readLock();
    private static final ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock
            .writeLock();
    private static void read() {
        System.out.println(Thread.currentThread().getName() + "尝试得到读锁");
        readLock.lock();
        try {
            System.out.println(Thread.currentThread().getName() + "得到读锁，正在读取");
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + "释放读锁");
            readLock.unlock();
        }
    }
    private static void write() {
        log.info(Thread.currentThread().getName() + "尝试得到写锁");
        writeLock.lock();
        try {
            log.info(Thread.currentThread().getName() + "得到写锁，正在写入");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.info(Thread.currentThread().getName() + "释放写锁");
            writeLock.unlock();
        }
    }
    private static void readAndWrite() {
        readLock.lock();
        //在获取写锁之前，必须首先释放读锁。
        log.info(Thread.currentThread().getName() + "得到读锁，正在读取");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.info(Thread.currentThread().getName() + "释放读锁");
            readLock.unlock();
        }
        writeLock.lock();
        log.info(Thread.currentThread().getName() + "得到写锁，正在写入");
        try {
            //这里需要再次判断数据的有效性,因为在我们释放读锁和获取写锁的空隙之内，可能有其他线程修改了数据。
            TimeUnit.SECONDS.sleep(5);
            //在不释放写锁的情况下，直接获取读锁，这就是读写锁的降级。
            readLock.lock();
            log.info(Thread.currentThread().getName() + "得到读锁");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放了写锁，但是依然持有读锁
            log.info(Thread.currentThread().getName() + "释放写锁");
            writeLock.unlock();
        }
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //释放读锁
            log.info(Thread.currentThread().getName() + "释放读锁");
            readLock.unlock();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> read(),"Thread-2").start();
        new Thread(() -> write(),"Thread-3").start();
        new Thread(() -> read(),"Thread-4").start();
        new Thread(() -> read(),"Thread-5").start();
        new Thread(() -> read(),"Thread-6").start();
        // new Thread(() -> readAndWrite(),"Thread-2").start();
        // new Thread(() -> readAndWrite(),"Thread-3").start();
        // new Thread(() -> readAndWrite(),"Thread-4").start();
        // new Thread(() -> readAndWrite(),"Thread-5").start();
        // new Thread(() -> readAndWrite(),"Thread-6").start();
        // log.info("MAX_READ_LOCK_COUNT {}", (1 << 16) - 1);
        // ExecutorService executorService = Executors.newFixedThreadPool(65540);
        // try {
        //     for (int i = 0; i < 656; i++) {
        //         new Thread(() -> read(), "Thread-" + i).start();
        //         // executorService.execute(() -> read());
        //     }
        // } finally {
        //     executorService.shutdown();
        // }
    }
}
