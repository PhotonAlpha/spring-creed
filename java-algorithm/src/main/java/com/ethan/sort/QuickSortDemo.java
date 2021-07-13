package com.ethan.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className: QuickSortDemo
 * @author: Ethan
 * @date: 5/7/2021
 **/
public class QuickSortDemo {
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public static void main(String[] args) {
        // new CopyOnWriteArrayList<>();
        // int[] arr = new int[]{-9, 78, 0, 23, -567, 70};
        //测试冒泡排序的速度O(n^2)
        int[] arr = new int[800_000];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(800_000);
        }
        // System.out.println("排序前：" + Arrays.toString(arr));
        long start = System.currentTimeMillis();
        quickSort(arr, 0, arr.length - 1);
        long end = System.currentTimeMillis();
        System.out.println("耗费时间" + (end - start) + "ms");
        // System.out.println("排序后：" + Arrays.toString(arr));
        for (int i = 0; i < 10; i++) {
            System.out.print(arr[i]+",");

        }
    }

    private static void quickSort(int[] arr, int left, int right) {
        int l = left;
        int r = right;
        //pivot中轴值
        int pivot = arr[(left + right) / 2];
        // 让比pivot值小放左边
        // 让比pivot值大放右边
        while (l < r) {
            // 在pivot左边找到大于pivot值，才退出
            while (arr[l] < pivot) {
                l += 1;
            }
            // 在pivot右边找到小于等于pivot值，才退出
            while (arr[r] > pivot) {
                r -= 1;
            }
            //如果l >=r 说明已经找到 pivot的值
            if (l >= r) {
                break;
            }
            //交换
            int tmp = arr[l];
            arr[l] = arr[r];
            arr[r] = tmp;

            //如果交完换发现arr[l] == pivot 前移一下 r--
            if (arr[l] == pivot) {
                r -= 1;
            }
            //如果交换完发现arr[r] == pivot 后移一下 l++
            if (arr[r] == pivot) {
                l += 1;
            }
        }
        //如果l == r,必须l++, r--,否则会栈溢出
        if (l == r) {
            l += 1;
            r -= 1;
        }
        //向左递归
        if (left < r) {
            quickSort(arr, left, r);
        }
        if (right > l) {
            quickSort(arr, l, right);
        }
    }


}
