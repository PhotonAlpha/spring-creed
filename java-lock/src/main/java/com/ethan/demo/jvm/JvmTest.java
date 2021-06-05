package com.ethan.demo.jvm;

import java.util.concurrent.TimeUnit;

/**
 * @className: JvmTest
 * @author: Ethan
 * @date: 26/5/2021
 **/
public class JvmTest {
    public static void main(String[] args) {
        System.out.println("Hello GC***");
        // byte[] bytes = new byte[50 * 1024 * 1024];
        try {
            TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
