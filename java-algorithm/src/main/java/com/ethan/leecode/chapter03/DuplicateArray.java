/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.leecode.chapter03;

/**
 * 数组中的重复数字
 *
 * 在一个长度为n的数组nums里的所有数字都在0~n-1的范围内
 * 数组中的某些数字是重复的，但是不知道有几个数字重复了，也不知道每个数字重复了几次。
 * 请找出数组中任意一个重复的数字
 *
 * [2,3,1,0,2,5,3]
 * 输出2 或3
 *
 * 限制
 * 2<= n <= 100000
 */
public class DuplicateArray {
    public static void main(String[] args) {
        int[] nums = new int[]{2, 3, 1, 0, 2, 5, 3};
        // 1320253
        // 3120253
        // 0123253
        System.out.println(diff(nums));

    }

    public static int diff(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            if (nums[i] == i) {
                i++;
                continue;
            }
            if (nums[i] == nums[nums[i]])
                return nums[i];
            int tmp = nums[nums[i]];
            nums[nums[i]] = nums[i];
            nums[i] = tmp;
        }
        return -1;
    }
}
