package com.ethan.demo.jvm;

import java.lang.ref.SoftReference;

/**
 * @className: SoftReferenceDemo
 * @author: Ethan
 * @date: 27/5/2021
 **/
public class SoftReferenceDemo {
    /**
     * 内存够用的时候就保留，不够的时候就回收
     * -Xms10m -Xmx10m
     */
    public static void softRefMemoryEnough() {
        Object o1 = new Object();
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());


        o1 = null;
        System.gc();

        System.out.println(o1);
        System.out.println(softReference.get());
        System.out.println("---------");
    }

    /**
     *
     */
    public static void softRefMemoryNotEnough() {
        Object o1 = new Object();
        SoftReference<Object> softReference = new SoftReference<>(o1);
        System.out.println(o1);
        System.out.println(softReference.get());

        o1 = null;
        try {
            byte[] bytes = new byte[50 * 1024 * 1024];
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(o1);
            System.out.println(softReference.get());
            System.out.println("---------");
        }

    }
    public static void main(String[] args) {
        softRefMemoryEnough();

        softRefMemoryNotEnough();
    }
}
