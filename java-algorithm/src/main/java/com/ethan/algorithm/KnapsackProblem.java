package com.ethan.algorithm;

/**
 * @className: KnapsackProblem
 * @author: Ethan
 * @date: 22/8/2021
 *
 * 背包问题-动态规划算法
 **/
public class KnapsackProblem {
    public static void main(String[] args) {
        int[] w = {1, 4, 3};//物品的重量
        int[] val = {1500, 3000, 2000};//物品的价值
        int m = 4;//背包的容量
        int n  = val.length; //物品的个数

        //为了记录放入商品的情况,我们定义二维数组
        int[][] path = new int[n + 1][m + 1];

        //创建二维数组
        // v[i][j] 表示在前1个物品中能够装入容量为j的背包中的最大值
        int[][] v = new int[n + 1][m + 1];

        //初始化第一行 第一列,本程序可以不去处理
        for (int i = 0; i < v.length; i++) {
            v[i][0] = 0;//第一列设置0
        }
        for (int i = 0; i < v[0].length; i++) {
            v[0][i] = 0;//第一行设置0
        }
        //根据公式动态规划处理
        for (int i = 1; i < v.length; i++) {
            for (int j = 1; j < v[0].length; j++) {
                //公式
                if (w[i - 1] > j) {
                    v[i][j] = v[i - 1][j];
                } else {
                    //因为i是从1开始,公式如下
                    // v[i][j] = Math.max(v[i - 1][j], val[i - 1] + v[i - 1][j - w[i - 1]]);
                    // 为了记录商品存放到背包的情况,不能简单的使用公式,可以通过if-else来体现公式
                    if (v[i - 1][j] < val[i - 1] + v[i - 1][j - w[i - 1]]) {
                        v[i][j] = val[i - 1] + v[i - 1][j - w[i - 1]];
                        //记录当前path
                        path[i][j] = 1;
                    } else {
                        v[i][j] = v[i - 1][j];
                    }
                }
            }
        }

        //输出v
        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < v[i].length; j++) {
                System.out.print(v[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println("-----------path=========");
        //输出path
        int i = path.length - 1;
        int j = path[0].length - 1;
        while (i > 0 && j > 0) {
            if (path[i][j] == 1) {
                System.out.printf("第%d个商品放入到背包\n", i);
                j -= w[i - 1];
            }
            i --;
        }
        for (int x = 0; x < path.length; x++) {
            for (int y = 0; y < path[x].length; y++) {
                System.out.print(path[x][y]+" ");
            }
            System.out.println();
        }


    }
}
