package com.ethan.demo.jvm;

/**
 * @className: StrongReferenceDemo
 * @author: Ethan
 * @date: 27/5/2021
 **/
public class StrongReferenceDemo {
    public static void main(String[] args) {
        Object o1 = new Object();//这样定义的默认就是强引用
        Object o2 = o1;//o2引用赋值
        o1 = null;//置空
        System.gc();
        System.out.println("--> " + o1);
        System.out.println("--> " + o2);
    }
}
