package com.ethan.sort;

import java.util.Arrays;
import java.util.Random;

/**
 * @className: SelectSortDemo
 * @author: Ethan
 * @date: 5/7/2021
 **/
public class SelectSortDemo {
    public static void main(String[] args) {
        //测试选择排序的速度O(n^2)
        // int[] arr = new int[]{1, 3, -1, 5, 10, 2};
        int[] arr = new int[80000];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(80000);
        }
        // System.out.println("排序前：" + Arrays.toString(arr));
        long start = System.currentTimeMillis();
        selectSort(arr);
        long end = System.currentTimeMillis();
        System.out.println("耗费时间" + (end - start) + "ms");
        // System.out.println("排序后：" + Arrays.toString(arr));
    }

    private static void selectSort(int[] arr) {
        /**
         * 优化
         * //时间复杂度 O(n^2)
         */
        System.out.println("优化-----");
        // 如果发现某次排序，可以提前结束
        boolean flag = false;
        for (int i = 0; i < arr.length; i++) {
            // System.out.println(Arrays.toString(arr));

            int minIndex = i;
            int min = arr[i];
            for (int j = i + 1; j < arr.length; j++) {
                if (min > arr[j]) {
                    flag = true;
                    minIndex = j;
                    min = arr[j];
                }
            }
            arr[minIndex] = arr[i];
            arr[i] = min;
            if (!flag) {
                break;
            } else {
                flag = false;//重置
            }
        }
    }
}
