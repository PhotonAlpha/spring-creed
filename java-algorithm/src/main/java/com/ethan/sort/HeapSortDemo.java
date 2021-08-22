package com.ethan.sort;

import java.util.Arrays;

/**
 * @className: HeapSortDemo
 * @author: Ethan
 * @date: 18/7/2021
 **/
public class HeapSortDemo {
    public static void main(String[] args) {
        //升序排列 大顶堆
        //降序排列 小顶堆
        int[] arr = {4, 6, 8, 5, 9};
        heapSort(arr);

    }

    //堆排序
    public static void heapSort(int[] arr) {
        System.out.println("堆排序");

        //分步完成
        // adjustHeap(arr, 1, arr.length);
        // System.out.println("第一次：" + Arrays.toString(arr));
        // adjustHeap(arr, 0, arr.length);
        // System.out.println("第二次：" + Arrays.toString(arr));

        // 最终完成
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            adjustHeap(arr, i, arr.length);
        }
        /**
         * 2. 将堆顶元素与末尾元素交换，将最大元素“沉”到数组末端
         * 3. 重新调整结构，使其满足堆定义，然后继续交换堆顶元素与当前末尾元素，反复执行调整+交换步骤，直到整个序列有序
         */
        int tmp = 0;
        for (int j = arr.length - 1; j > 0; j--) {
            //交换
            tmp = arr[j];
            arr[j] = arr[0];
            arr[0] = tmp;
            adjustHeap(arr, 0, j);
        }
        System.out.println("最终结果：" + Arrays.toString(arr));
    }

    //将一个数组(二叉树)调整成一颗大顶堆

    /**
     * 完成将以i对应的非叶子节点的树调整成大顶堆
     * arr = {4,6,8,5,9} => i=1 => 得到 {4,9,8,5,6}
     *          4            4          9           9
     *         / \          / \        / \         / \
     *        6   8  ==>   9   8 ==>  4   8  ==>  6   8
     *       / \          / \        / \         / \
     *      5   9        5   6      5   6       5   4
     *                                          {9,6,8,5,4}
     * @param arr 待调整的数组
     * @param i 表示非叶子节点在数组中的索引
     * @param length 表示对多少个元素进行调整， length在逐渐减少
     */
    public static void adjustHeap(int[] arr, int i, int length) {
        int tmp = arr[i];//先取出当前元素的值，保存在临时变量
        // 1. k= i*2+1, k是i节点的左子节点
        for (int k = i * 2 + 1; k < length; k = k * 2 + 1) {
            if (k + 1 < length && arr[k] < arr[k + 1]) {
                //说明左子节点小于右子节点
                k++;
            }
            if (arr[k] > tmp) {
                //如果子节点大于父节点
                // 把较大的值赋给当前节点
                arr[i] = arr[k];
                i = k; //!!! i指向k，继续循环比较
            } else {
                break;
            }
        }
        //当for循环结束后，以i为父节点的树的最大值，放在了最顶上
        arr[i] = tmp;//将tmp值放到调整后的位置
    }
}
