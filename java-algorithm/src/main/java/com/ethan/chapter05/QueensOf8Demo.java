package com.ethan.chapter05;


/**
 * 递归
 * 八皇后问题
 * 说明：理论上应该创建一个二维数组来表示棋盘，但是实际上可以通过算法，
 * 用一个一维数组即可解决问题．arr[8]={0,4,7,5,2,6,1,3},
 * 对应arr下表示 示第几行，即第几个皇后，arr[i]=val，val表示第i+1个皇后，放在第i+1行的第val+1列
 *
 * @className: PolandNotation
 * @author: Ethan
 * @date: 26/6/2021
 **/
public class QueensOf8Demo {
    // 定义max表示有多少个皇后
    // 定义arr，保存皇后放置位置的结果，比如 arr[8]={0,4,7,5,2,6,1,3}
    static int max = 8;
    static int[] array = new int[max];
    static int count = 0;
    public static void main(String[] args) {
        check(0);
        System.out.println("总数："+count);
    }

    // 编写方法，放置第N个皇后
    // ***特别注意***： check是每一次递归时，进入到check中都有 for (int i = 0; i < max; i++)
    // 因此会有回溯
    private static void check(int n) {
        if (n == max) { //n = 8, 已经是结果
            print();
            count++;
            return;
        }
        //依次放入皇后，并开始判断
        for (int i = 0; i < max; i++) {
            // 先把当前皇后n放到改行的第1列
            array[n] = i;
            // 判读放置第n个皇后的i列时，是否冲突
            if (checkConflict(n)) { //不冲突
                //接着放n+1
                check(n + 1);
            }
            // 如果冲突，继续执行array[n] = i;即将第n个皇后放置在本行的后移的一个位置
        }
    }
    //查看当前放置第n个皇后，
    //就去检测该皇后是否和前面已经摆放的皇后冲突
    private static boolean checkConflict(int n) {
        for (int i = 0; i < n; i++) {
            // 1. array[i] == array[n] 表示判断第n个皇后是否和前面n-1和皇后在同一列
            // 2. Math.abs(n - i) == Math.abs(array[n] - array[i]) 表示第n个皇后 和第i个皇后是否在统一斜线
            if (array[i] == array[n] || Math.abs(n - i) == Math.abs(array[n] - array[i])) {
                return false;
            }
        }
        return true;
    }

    //学一个方法，可以将数组
    private static void print() {
        for (int i = 0; i < array.length; i++) {
            System.out.print(array[i]+"\t");
        }
        System.out.println();
    }
}
