package com.ethan.sort;

import java.util.Arrays;
import java.util.Random;

/**
 * @className: ShellSort
 * @author: Ethan
 * @date: 5/7/2021
 **/
public class ShellSort {


    public static void main(String[] args) {
        // int[] arr = new int[]{8, 9, 1, 7, 2, 3, 5, 4, 6, 0};

        int[] arr = new int[80000];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(80000);
        }
        // System.out.println("排序前：" + Arrays.toString(arr));
        long start = System.currentTimeMillis();
        // shellSort(arr);
        shellSortShift(arr);
        long end = System.currentTimeMillis();
        System.out.println("耗费时间" + (end - start) + "ms");
        // System.out.println("排序后：" + Arrays.toString(arr));
    }

    /**
     * 移位法
     *
     * @param arr
     */
    private static void shellSortShift(int[] arr) {
        int tmp;
        //使用循环处理
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            // 从gap个元素，对其所在的组直接插入排序
            for (int i = gap; i < arr.length; i++) {
                int j = i;
                tmp = arr[j];
                if (arr[j] < arr[j - gap]) {
                    while (j - gap >= 0 && tmp < arr[j - gap]) {
                        arr[j] = arr[j - gap];
                        j -= gap;
                    }
                    //当while停止后，找到插入的位置
                    arr[j] = tmp;
                }
            }
        }
    }

    /**
     * 交换法
     * @param arr
     */
    private static void shellSort(int[] arr) {
        int tmp;
        //使用循环处理
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < arr.length; i++) {
                //遍历共5组，每组2个元素，步长5
                for (int j = i - gap; j >= 0; j -= gap) {
                    // 如果当前元素大于加上步长后的那个元素，说明交换
                    if (arr[j] > arr[j + gap]) {
                        tmp = arr[j];
                        arr[j] = arr[j + gap];
                        arr[j + gap] = tmp;
                    }
                }
            }
            // System.out.println(String.format("希尔排序第%s轮：%s", gap, Arrays.toString(arr)));
        }
    }

    //使用逐步推导来进行希尔排序
    private static void shellSortDerive(int[] arr) {
        // 1. 第一轮排序是将10个数据分成了5组
        int tmp;
        for (int i = 5; i < arr.length; i++) {
            //遍历共5组，每组2个元素，步长5
            for (int j = i - 5; j >= 0; j -= 5) {
                // 如果当前元素大于加上步长后的那个元素，说明交换
                if (arr[j] > arr[j + 5]) {
                    tmp = arr[j];
                    arr[j] = arr[j + 5];
                    arr[j + 5] = tmp;
                }
            }
        }


        // 2. 第二轮排序是将10个数据分成了5/2 = 2组
        for (int i = 2; i < arr.length; i++) {
            //遍历共5组，每组2个元素，步长5
            for (int j = i - 2; j >= 0; j -= 2) {
                // 如果当前元素大于加上步长后的那个元素，说明交换
                if (arr[j] > arr[j + 2]) {
                    tmp = arr[j];
                    arr[j] = arr[j + 2];
                    arr[j + 2] = tmp;
                }
            }
        }
        System.out.println("希尔排序第二轮：" + Arrays.toString(arr));

        //  第三轮排序是将10个数据分成了2/2 = 1组
        for (int i = 1; i < arr.length; i++) {
            //遍历共5组，每组2个元素，步长5
            for (int j = i - 1; j >= 0; j -= 1) {
                // 如果当前元素大于加上步长后的那个元素，说明交换
                if (arr[j] > arr[j + 1]) {
                    tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
        System.out.println("希尔排序第三轮：" + Arrays.toString(arr));
    }


}
