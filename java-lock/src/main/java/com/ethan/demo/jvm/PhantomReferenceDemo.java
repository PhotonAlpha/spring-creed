package com.ethan.demo.jvm;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

/**
 * @className: PhantomReferenceDemo
 * @author: Ethan
 * @date: 27/5/2021
 **/
public class PhantomReferenceDemo {
    public static void main(String[] args) throws InterruptedException {
        Object o1 = new Object();//这样定义的默认就是强引用
        ReferenceQueue<Object> referenceQueue = new ReferenceQueue<>();
        PhantomReference<Object> phantomReference = new PhantomReference<>(o1, referenceQueue);
        System.out.println(o1);
        System.out.println(phantomReference.get());
        System.out.println(referenceQueue.poll());

        o1 = null;
        System.gc();
        TimeUnit.SECONDS.sleep(5);
        System.out.println(o1);
        System.out.println(phantomReference.get());
        System.out.println(referenceQueue.poll());

        System.out.println("=====================");

        Object o2 = new Object();//这样定义的默认就是强引用
        ReferenceQueue<Object> referenceQueue2 = new ReferenceQueue<>();
        WeakReference<Object> weakReference = new WeakReference<>(o2, referenceQueue2);
        System.out.println(o2);
        System.out.println(weakReference.get());
        System.out.println(referenceQueue2.poll());
        System.out.println("---------");
        o2 = null;
        System.gc();
        TimeUnit.SECONDS.sleep(5);
        System.out.println(o2);
        System.out.println(weakReference.get());
        System.out.println(referenceQueue2.poll());

    }
}
