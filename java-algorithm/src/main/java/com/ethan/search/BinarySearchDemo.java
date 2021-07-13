package com.ethan.search;


import java.util.ArrayList;
import java.util.List;

/**
 * @className: BinarySearchDemo
 * @author: Ethan
 * @date: 10/7/2021
 **/
public class BinarySearchDemo {
    public static void main(String[] args) {
        int[] arr = {1, 3, 5, 7, 9,9};
        int result = binarySearch(arr, 0, arr.length - 1, 9);
        System.out.println("找到的index:" + result);
        List<Integer> res = binarySearchDuplicate(arr, 0, arr.length - 1, 9);
        System.out.println("找到的index:" + res);

    }

    /**
     * 二分查找算法
     *
     * @param arr
     * @param left
     * @param right
     * @param value 要查找的值
     * @return 如果找到，就返回下标，如果没有找到，就返回-1
     */
    private static int binarySearch(int[] arr, int left, int right, int value) {
        // 如果left>right 表示递归完毕
        if (right < left) {
            return -1;
        }
        int mid = (left + right) / 2;
        if (value < arr[mid]) {
            //从左边开始查找
            return binarySearch(arr, left, mid - 1, value);
        } else if (value > arr[mid]) {
            //从右边开始查找
            return binarySearch(arr, mid + 1, right, value);
        } else {
            return mid;
        }
    }

    /**
     * 二分查找算法
     *
     * @param arr
     * @param left
     * @param right
     * @param value 要查找的值
     * @return 如果找到，就返回下标，如果没有找到，就返回-1
     */
    private static List<Integer> binarySearchDuplicate(int[] arr, int left, int right, int value) {
        List<Integer> list = new ArrayList<>();
        // 如果left>right 表示递归完毕
        if (right < left) {
            return new ArrayList<>();
        }
        int mid = (left + right) / 2;
        if (value < arr[mid]) {
            //从左边开始查找
            return binarySearchDuplicate(arr, left, mid - 1, value);
        } else if (value > arr[mid]) {
            //从右边开始查找
            return binarySearchDuplicate(arr, mid + 1, right, value);
        } else {
            int temp = mid - 1;
            while (true) {
                if (temp < 0 || arr[temp] != value) {
                    break;
                }
                list.add(temp);
                temp--;
            }
            list.add(mid);

            temp = mid + 1;
            while (true) {
                if (temp > arr.length - 1 || arr[temp] != value) {
                    break;
                }
                list.add(temp);
                temp++;
            }
            return list;
        }
    }
}
