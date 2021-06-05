package com.ethan.demo.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className: ReentrantLockDemo
 * @author: Ethan
 * @date: 1/5/2021
 **/
public class ReentrantLockDemo {
    public static void main(String[] args) {
        Lock lock = new ReentrantLock();
        Phone phone = new Phone();
        PhoneRun phoneRun = new PhoneRun();
        for (int i = 1; i <= 10; i++) {
            // new Thread(() -> {
            //     phone.sendSMS();
            // }, "t"+i).start();

            new Thread(phoneRun, "PhoneRun" + i).start();
        }
    }

}

class Phone {
    public synchronized void sendSMS() {
        System.out.println(Thread.currentThread().getId()+"\t ####sendSMS");
        sendEmail();
    }
    public synchronized void sendEmail() {
        System.out.println(Thread.currentThread().getId()+"\t ####sendEmail");
    }
}

class PhoneRun implements Runnable {
    Lock lock = new ReentrantLock();
    public void sendSMS() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getId()+"\t ####sendSMS");
            sendEmail();
        } finally {
            lock.unlock();
        }
    }
    public void sendEmail() {
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getId()+"\t ####sendEmail");
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void run() {
        sendSMS();
    }
}
