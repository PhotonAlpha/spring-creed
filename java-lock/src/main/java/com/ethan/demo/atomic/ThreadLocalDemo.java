/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.demo.atomic;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/9/2022 6:31 PM
 */
public class ThreadLocalDemo {
    public static void main(String[] args) throws InterruptedException {
        ThreadLocal<String> LOCAL = new ThreadLocal<>();
        SoftReference<ThreadLocal<String>> softReference = new SoftReference<>(LOCAL);
        LOCAL.set("abc");
        LOCAL = null;
        // byte[] bytes = new byte[40 * 1024 * 1024];
        System.gc();
        String s = softReference.get().get();
        System.out.println("get result=" + s);
        // Thread thread = new Thread(new Task());
        // thread.start();
        // thread.join();
        System.out.println("===end");



/*         Car car = new Car(22000,"silver");
        WeakReference<Car> weakCar = new WeakReference<>(car);
        // SoftReference<Car> weakCar = new SoftReference<>(car);
        car = null;
        System.out.println(weakCar.get());
        System.gc();
        System.out.println(weakCar.get()); */

    }

    static class Task implements Runnable {
        @Override
        public void run() {
            /* LOCAL.set("abc");
            System.gc();
            String s = LOCAL.get();
            System.out.println("get result=" + s); */
        }
    }

    static class Car {
        private double price;
        private String colour;

        public Car(double price, String colour) {
            this.price = price;
            this.colour = colour;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getColour() {
            return colour;
        }

        public void setColour(String colour) {
            this.colour = colour;
        }

        @Override
        public String toString() {
            return "Car{" +
                    "price=" + price +
                    ", colour='" + colour + '\'' +
                    '}';
        }
    }

}
