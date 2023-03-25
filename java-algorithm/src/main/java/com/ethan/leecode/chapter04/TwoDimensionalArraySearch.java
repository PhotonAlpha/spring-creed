/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.leecode.chapter04;

/**
 * 在一个 n * m 的二维数组中，每一行都按照从左到右 <b>非递减</b> 的顺序排序,
 * 每一列都按照从上倒下 <b>非递减</b> 的顺序排序。<br/>
 * 请完成一个高效的函数，输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。<br/>
 * <br/>
 * 示例：<br/>
 * 现有矩阵 matrix如下：<br/>
 * [<br/>
 *  [ 1, 4, 7,11,15],<br/>
 *  [ 2, 5, 8,12,19],<br/>
 *  [ 3, 6, 9,16,22],<br/>
 *  [10,13,14,17,24],<br/>
 *  [18,21,23,26,30],<br/>
 * ]<br/>
 * <br/>
 * 给定target=5，返回true<br/>
 * 给定target=20,返回false<br/>
 * <br/>
 * 限制<br/>
 * 0 <= n <= 1000<br/>
 * 0 <= m <= 1000<br/>
 */
public class TwoDimensionalArraySearch {
    public static void main(String[] args) {
        int[][] matrix = new int[][]{
                {1, 4, 7, 11, 15},
                {2, 5, 8, 12, 19},
                {3, 6, 9, 16, 22},
                {10, 13, 14, 17, 24},
                {18, 21, 23, 26, 30}
        };
        System.out.println(calculate(matrix, 5));
        System.out.println(calculate(matrix, 20));
    }

    public static boolean calculate(int[][] matrix, int target) {
        return false;
    }
}
