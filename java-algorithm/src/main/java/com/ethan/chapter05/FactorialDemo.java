package com.ethan.chapter05;


/**
 * 递归
 * @className: PolandNotation
 * @author: Ethan
 * @date: 26/6/2021
 **/
public class FactorialDemo {
    public static void main(String[] args) {
        // System.out.println((100 * 99) % Integer.MAX_VALUE);
        System.out.println(Integer.MAX_VALUE);
        System.out.println("计算的结果是:" + factorial(100));
    }

    public static int factorial(int n) {
        if (n < 2) {
            return 1;
        } else {
            return (factorial(n - 1) % 9999) + (n % 9999);
        }
    }

}
