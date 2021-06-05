package com.ethan.demo.jvm;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * @className: WeakReferenceDemo
 * @author: Ethan
 * @date: 27/5/2021
 **/
public class WeakReferenceDemo {
    /**
     * 只要GC就回收
     */
    public static void weakRefMemoryEnough() {
        Object o1 = new Object();
        WeakReference<Object> weakReference = new WeakReference<>(o1);
        System.out.println(o1);
        System.out.println(weakReference.get());


        o1 = null;
        System.gc();

        System.out.println(o1);
        System.out.println(weakReference.get());
        System.out.println("---------");
    }

    public static void weakHashMap() {
        HashMap<Object, Object> hashMap = new HashMap<>();
        Integer key = new Integer(1);
        String value = "HashMap";
        hashMap.put(key, value);

        key = null;

        System.out.println("hashMap:" + hashMap);
        System.gc();
        System.out.println("hashMap:" + hashMap);

        WeakHashMap<Object, Object> weakHashMap = new WeakHashMap<>();

        Integer weakKey = new Integer(2);
        String weakValue = "HashMap2";
        weakHashMap.put(weakKey, weakValue);

        weakKey = null;

        System.out.println("weakHashMap:" + weakHashMap);
        System.gc();
        System.out.println("weakHashMap:" + weakHashMap);
    }

    public static void main(String[] args) {
        weakRefMemoryEnough();

        weakHashMap();
    }
}
