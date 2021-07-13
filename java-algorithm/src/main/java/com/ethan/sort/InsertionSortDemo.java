package com.ethan.sort;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

/**
 * @className: InsertionSortDemo
 * @author: Ethan
 * @date: 5/7/2021
 **/
public class InsertionSortDemo {
    public static void main(String[] args) {
        Arrays.asList(1, 2, 3).sort((h1, h2) -> h1.compareTo(h2));

        // int[] arr = new int[]{1, 3, -1, 5, 10, 2};
        int[] arr = new int[80000];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(80000);
        }
        // System.out.println("排序前：" + Arrays.toString(arr));
        long start = System.currentTimeMillis();
        insertion(arr);
        long end = System.currentTimeMillis();
        System.out.println("耗费时间" + (end - start) + "ms");
        // System.out.println("排序后：" + Arrays.toString(arr));
        // System.out.println(Arrays.toString(arr));
    }


    private static void insertion(int[] arr) {
        int insertionVal;
        int insertionIndex;
        for (int i = 1; i < arr.length; i++) {
            insertionVal = arr[i];
            insertionIndex = i - 1;
            while (insertionIndex >= 0 && insertionVal < arr[insertionIndex]) {
                arr[insertionIndex + 1] = arr[insertionIndex];
                insertionIndex--;
            }
            //当退出循环时，说明插入的位置找到， 即insertionIndex+1
            arr[insertionIndex + 1] = insertionVal;
        }
    }

}
