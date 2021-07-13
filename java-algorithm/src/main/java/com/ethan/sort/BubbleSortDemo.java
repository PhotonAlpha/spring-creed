package com.ethan.sort;

import java.util.Arrays;
import java.util.Random;

/**
 * @className: BubbleSortDemo
 * @author: Ethan
 * @date: 5/7/2021
 **/
public class BubbleSortDemo {
    public static void main(String[] args) {
        //测试冒泡排序的速度O(n^2)
        int[] arr = new int[80000];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(80000);
        }
        System.out.println("排序前：" + Arrays.toString(arr));
        long start = System.currentTimeMillis();
        bubbleSort(arr);
        long end = System.currentTimeMillis();
        System.out.println("耗费时间" + (end - start) + "ms");
        System.out.println("排序后：" + Arrays.toString(arr));
    }

    private static void bubbleSort(int[] arr) {
        /**
         * 优化
         * //时间复杂度 O(n^2)
         */
        System.out.println("优化-----");
        // 如果发现某次排序，可以提前结束
        boolean flag = false;
        int tmp;
        for (int i = 0; i < arr.length - 1; i++) {
            // System.out.println(Arrays.toString(arr));
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    flag = true;
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
            if (!flag) {
                break;
            } else {
                flag = false;//重置
            }
        }
    }
}
