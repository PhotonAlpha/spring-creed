package com.ethan.sort;

import java.util.Arrays;
import java.util.Random;

/**
 * @className: MergeSortDemo
 * @author: Ethan
 * @date: 8/7/2021
 **/
public class MergeSortDemo {
    public static void main(String[] args) {
        // int[] arr = {8, 4, 5, 7, 1, 3, 6, 2};
        int[] arr = new int[80000];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(80000);
        }
        int[] tmp = new int[arr.length];//归并排序需要额外的空间
        // System.out.println("排序前：" + Arrays.toString(arr));
        long start = System.currentTimeMillis();
        mergeSort(arr, 0, arr.length - 1, tmp);
        long end = System.currentTimeMillis();
        System.out.println("耗费时间" + (end - start) + "ms");

        // System.out.println("归并排序后=" + Arrays.toString(arr));
    }

    public static void mergeSort(int[] arr, int left, int right, int[] tmp) {
        if (left < right) {
            int mid = (left + right) / 2; //中间值
        //    向左递归分解
            mergeSort(arr, left, mid, tmp);
            //向右递归分解
            mergeSort(arr, mid + 1, right, tmp);
            //每分解一次，就合并一次
            mergeOrder(arr, left, mid, right, tmp);
        }
    }
    /**
     * 合并方法
     * @param arr 排序的原始数据
     * @param left 左边有序序列的初始索引
     * @param mid 中间索引
     * @param right 右边索引
     * @param temp 中转数组
     */
    private static void mergeOrder(int[] arr, int left, int mid, int right, int[] temp) {
        int i = left; //初始化i, 左边有序序列的索引
        int j = mid + 1; //初始化j, 右边有序序列的索引
        int t = 0; // 指向temp[]的当前索引
        // 1. 先把左右两边的数据按规则 填充到tmp数组，
        //直到左右两边的有序序列，有一边处理完毕为止
        while (i <= mid && j <= right) {
            // 如果左边的有序序列的当前元素，小于等于右边有序序列的当前元素
            //即将左边的当前元素填充到temp数组
            //然后 t++ i++
            if (arr[i] <= arr[j]) {
                temp[t] = arr[i];
                t++;
                i++;
            } else {
                temp[t] = arr[j];
                t++;
                j++;
            }
        }
        //2.把剩余数据的一边的数据一次全部填充到temp
        while (i <= mid) {
            //左边的有序序列还有剩余的元素，就全部填充到tmp
            temp[t] = arr[i];
            t++;
            i++;
        }
        while (j <= right) {
            //左边的有序序列还有剩余的元素，就全部填充到tmp
            temp[t] = arr[j];
            t++;
            j++;
        }
        //3.将temp数组 复制回arr
        t = 0;
        int tempLeft = left;
        // System.out.println("tempLeft：" + tempLeft + " right:" + right);
        while (tempLeft <= right) {//第一次合并 tempLeft =0 right =1
            arr[tempLeft++] = temp[t++];
            // t++;
            // tempLeft++;
        }
    }


}
