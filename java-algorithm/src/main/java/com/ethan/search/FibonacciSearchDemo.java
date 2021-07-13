package com.ethan.search;

import java.util.Arrays;

/**
 * @className: FibonacciSearchDemo
 * @author: Ethan
 * @date: 11/7/2021
 **/
public class FibonacciSearchDemo {
    private static int maxSize = 20;

    public static void main(String[] args) {
        int[] arr = {1, 8, 10, 89, 1000, 1234};
        System.out.println("最终结果：" + fibonacciSearchNoRecursion(arr, 1000));
        System.out.println("最终结果：" + fibonacciSearchNoRecursion(arr, 1001));
    }

    /**
     * @param arr 数组
     * @param key 需要查找的值
     * @return 返回对应下标，若果没有就返回-1
     */
    private static int fibonacciSearchNoRecursion(int[] arr, int key) {
        int low = 0;
        int high = arr.length - 1;
        int k = 0;//表示斐波那契分割数值的下标
        int mid = 0;
        int f[] = fib();
        //获取到分割数值的下标
        while (high > f[k] - 1) {
            k++;
        }
        // f[k]可能大于arr的长度，因此需要使用Arrays类，构造新的数组，并指向tmp
        // 不足部分需要使用0填充
        int[] tmp = Arrays.copyOf(arr, f[k]);
        //实际上需求使用a数组最后的数填充temp
        // int[] arr = {1, 8, 10, 89, 1000, 1234, 0 , 0 , 0};
        // --> int[] arr = {1, 8, 10, 89, 1000, 1234, 1234 , 1234 , 1234};
        for (int i = high + 1; i < tmp.length; i++) {
            tmp[i] = arr[high];
        }
        //使用循环来查找key
        while (low <= high) {
            mid = low + f[k - 1] - 1;
            if (key < tmp[mid]) {
                //继续向arr前面查找
                high = mid - 1;
                //1.全部元素=前面的元素+后面的元素
                // 2. f[k] = f[k-1]+f[k-2]
                // 因为 前面有f[k-1]个元素，所以可以继续拆分f[k-1] = f[k-2]+f[k-3]
                // 即在f[k-1]的前面继续查找
                k--;
            } else if (key > tmp[mid]) {
                //继续向arr后面查找
                low = mid + 1;
                //1.全部元素=前面的元素+后面的元素
                // 2. f[k] = f[k-1]+f[k-2]
                // 因为 后面有f[k-2]个元素，所以可以继续拆分f[k-2] = f[k-3]+f[k-4]
                // 即在f[k-2]的前面继续查找
                // 下次循环 mid = [f-1-2] -1
                k-=2;
            } else {
                // 需要确定返回的下标
                if (mid <= high) {
                    return mid;
                } else {
                    return high;
                }
            }
        }
        return -1;
    }
    /**
     * 因为后面mid=low+F(k-1)-1，需要使用到斐波那契数列，因此我们需要先获取到一个斐波那契数列
     */
    private static int[] fib() {
        int[] f = new int[maxSize];
        f[0] = 1;
        f[1] = 1;
        for (int i = 2; i < maxSize; i++) {
            f[i] = f[i - 1] + f[i - 2];
        }
        return f;
    }
}
