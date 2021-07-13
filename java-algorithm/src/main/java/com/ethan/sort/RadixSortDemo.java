package com.ethan.sort;

import java.util.Arrays;
import java.util.Random;

/**
 * @className: RadixSortDemo
 * @author: Ethan
 * @date: 8/7/2021
 **/
public class RadixSortDemo {
    public static void main(String[] args) {
        // int[] arr = {53, 3, 542, 748, 14, 214};
        int[] arr = new int[8000_000];
        Random random = new Random();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(8000_000);
        }
        // System.out.println("排序前：" + Arrays.toString(arr));
        long start = System.currentTimeMillis();
        // radixSortDerive(arr);
        radixSort(arr);
        long end = System.currentTimeMillis();
        System.out.println("耗费时间" + (end - start) + "ms");

        // System.out.println(Arrays.toString(arr));
    }

    private static void radixSort(int[] arr) {
        //1.先得到数组中最大的数的位数
        int max = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        //得到最大数的位数
        int maxLength = (max + "").length();
        int[][] bucket = new int[10][arr.length];
        //为了记录每个桶中实际存放了多少个数据，定义一个一维数组来记录各个桶每次放入的个数
        int[] bucketElementCounts = new int[10];

        for (int i = 0, n = 1; i < maxLength; i++, n *= 10) {
            //第一轮（针对每个元素的个位进行排序处理）
            for (int j = 0; j < arr.length; j++) {
                // int digitOfElement = arr[j] / (int) Math.pow(10, i) % 10;
                int digitOfElement = arr[j] / n % 10;
                //放入到对应的桶中
                bucket[digitOfElement][bucketElementCounts[digitOfElement]] = arr[j];
                bucketElementCounts[digitOfElement]++;
            }
            //按照桶的顺序，放入原来的数组
            int index = 0;
            //遍历每一个桶，并将桶中的数据放入到原数组
            for (int k = 0; k < bucketElementCounts.length; k++) {
                //如果桶中有数据，才放入到原数组
                if (bucketElementCounts[k] != 0) {
                    for (int l = 0; l < bucketElementCounts[k]; l++) {
                        //取出元素放入到arr中
                        arr[index++] = bucket[k][l];
                    }
                }
                // 第一轮处理后，需要将每个bucketElementCounts[k] 置零
                bucketElementCounts[k] = 0;
            }
        }
    }

    //基数排序法
    private static void radixSortDerive(int[] arr) {
        //第一轮（针对每个元素的个位进行排序处理）
        // 定义一个二维数组，表示10个桶，每个桶就是一个一维数组
        // 1. 二维数组包含10个1维数组
        // 2. 为了防止放入数的时候，数据溢出，则每一个一维数组（桶），大小定为arr.length
        /**3.基数排序是使用空间换时间的经典算法*/
        int[][] bucket = new int[10][arr.length];

        //为了记录每个桶中实际存放了多少个数据，定义一个一维数组来记录各个桶每次放入的个数
        int[] bucketElementCounts = new int[10];

        //第一轮（针对每个元素的个位进行排序处理）
        for (int j = 0; j < arr.length; j++) {
            int digitOfElement = arr[j] % 10;
            //放入到对应的桶中
            bucket[digitOfElement][bucketElementCounts[digitOfElement]] = arr[j];
            bucketElementCounts[digitOfElement]++;
        }
        //按照桶的顺序，放入原来的数组
        int index = 0;
        //遍历每一个桶，并将桶中的数据放入到原数组
        for (int k = 0; k < bucketElementCounts.length; k++) {
            //如果桶中有数据，才放入到原数组
            if (bucketElementCounts[k] != 0) {
                for (int l = 0; l < bucketElementCounts[k]; l++) {
                //取出元素放入到arr中
                    arr[index++] = bucket[k][l];
                }
            }
            // 第一轮处理后，需要将每个bucketElementCounts[k] 置零
            bucketElementCounts[k] = 0;
        }

        //第二轮（针对每个元素的个位进行排序处理）
        for (int j = 0; j < arr.length; j++) {
            int digitOfElement = arr[j] / 10 % 10;
            //放入到对应的桶中
            bucket[digitOfElement][bucketElementCounts[digitOfElement]] = arr[j];
            bucketElementCounts[digitOfElement]++;
        }
        //按照桶的顺序，放入原来的数组
        index = 0;
        //遍历每一个桶，并将桶中的数据放入到原数组
        for (int k = 0; k < bucketElementCounts.length; k++) {
            //如果桶中有数据，才放入到原数组
            if (bucketElementCounts[k] != 0) {
                for (int l = 0; l < bucketElementCounts[k]; l++) {
                    //取出元素放入到arr中
                    arr[index++] = bucket[k][l];
                }
            }
            bucketElementCounts[k] = 0;
        }

        //第三轮（针对每个元素的个位进行排序处理）
        for (int j = 0; j < arr.length; j++) {
            int digitOfElement = arr[j] / 100 % 10;
            //放入到对应的桶中
            bucket[digitOfElement][bucketElementCounts[digitOfElement]] = arr[j];
            bucketElementCounts[digitOfElement]++;
        }
        //按照桶的顺序，放入原来的数组
        index = 0;
        //遍历每一个桶，并将桶中的数据放入到原数组
        for (int k = 0; k < bucketElementCounts.length; k++) {
            //如果桶中有数据，才放入到原数组
            if (bucketElementCounts[k] != 0) {
                for (int l = 0; l < bucketElementCounts[k]; l++) {
                    //取出元素放入到arr中
                    arr[index++] = bucket[k][l];
                }
            }
            bucketElementCounts[k] = 0;
        }
    }


}
