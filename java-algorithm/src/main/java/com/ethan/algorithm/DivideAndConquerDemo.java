package com.ethan.algorithm;

/**
 * @className: DivideAndConquerDemo
 * @author: Ethan
 * @date: 3/8/2021
 *
 * 分治算法
 *
 * 汉诺塔思路分析   移动次数 = 2^N - 1
 * 1. 如果只有一个盘，A -> C
 *    如果有 n >= 2 情况，我们总是可以看做是两个盘。 1.最下面的盘 2.上面的盘
 *    1.1. 先把坐上的盘A->B
 *    1.2. 把最下边的盘A->C
 *    1.3. 把B所有盘从B->C
 **/
public class DivideAndConquerDemo {
    public static int count = 0;
    public static void main(String[] args) {
        hanoiTower(5, 'A', 'B', 'C');
    }

    //汉诺塔的移动的方法，使用分治算法
    public static void hanoiTower(int num, char a, char b, char c) {
        // 如果只有一个盘
        if (num == 1) {
            count++;
            System.out.println(count + ". 第1个盘从" + a + "->" + c);
        } else {
            // 如果有 n >= 2 情况，我们总是可以看做是两个盘。
            // 1. 先把做上面的所有盘A->B,移动过程会使用到C
            hanoiTower(num - 1, a, c, b);
            // 把最下边的盘A->C
            count++;
            System.out.println(count + ". 第" + num + "个盘从" + a + "->" + c);
            //把B所有盘从B->C,移动过程使用到A
            hanoiTower(num - 1, b, a, c);
        }
    }
}
