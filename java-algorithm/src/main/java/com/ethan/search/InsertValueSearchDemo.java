package com.ethan.search;

/**
 * @className: InsertValueSearchDemo
 * @author: Ethan
 * @date: 11/7/2021
 **/
public class InsertValueSearchDemo {
    public static void main(String[] args) {
        // int[] arr = new int[100];
        // for (int i = 0; i < arr.length; i++) {
        //     arr[i] = i + 1;
        // }
        int[] arr = {1, 3, 5, 7, 9,9};
        int valueSearch = insertValueSearch(arr, 0, arr.length - 1, 7);
        System.out.println("找到下标：" + valueSearch);
    }

    /**
     * 插值查找算法
     *
     * @param arr
     * @param left
     * @param right
     * @param findVal 查找的值
     */
    private static int insertValueSearch(int[] arr, int left, int right, int findVal) {
        System.out.println("查找次数");
        // 数值可能越界
        if (left > right || findVal < arr[0] || findVal > arr[arr.length - 1]) {
            return -1;
        }
        //求出mid
        int mid = left + (right - left) * (findVal - arr[left]) / (arr[right] - arr[left]);
        int midVal = arr[mid];
        if (findVal > midVal) {
            //如果大于midVal,说明应该向右递归
            return insertValueSearch(arr, mid + 1, right, findVal);
        } else if (findVal < midVal) {
            return insertValueSearch(arr, left, mid - 1, findVal);
        } else {
            return mid;
        }
    }
}
