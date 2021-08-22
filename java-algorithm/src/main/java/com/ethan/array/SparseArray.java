package com.ethan.array;

/**
 * @className: SparseArray
 * @author: Ethan
 * @date: 20/6/2021
 *
 * 稀疏数组
 **/
public class SparseArray {
    public static void main(String[] args) {
        int chessArray1[][] = new int[11][11];
        chessArray1[1][2] = 1;
        chessArray1[2][3] = 2;
        chessArray1[3][4] = 1;
        // 输出原始二维数组
        System.out.println("原始的二维数组");
        for (int[] row : chessArray1) {
            for (int data : row) {
                System.out.printf("%d\t", data);
            }
            System.out.println();
        }
        //转换稀疏数组
        System.out.println("转换稀疏数组");
        int sum = 0;
        for (int[] row : chessArray1) {
            for (int data : row) {
                if (data > 0) {
                    sum++;
                }
            }
        }
        System.out.println("sum:" + sum);
        //创建稀疏数组
        int sparseArray[][] = new int[sum + 1][3];
        sparseArray[0][0] = 11;
        sparseArray[0][1] = 11;
        sparseArray[0][2] = sum;
        int count = 1;//用于记录是第几个非0数据
        for (int i = 0; i < chessArray1.length; i++) {
            for (int j = 0; j < chessArray1[i].length; j++) {
                if (chessArray1[i][j] > 0) {
                    sparseArray[count][0] = i;
                    sparseArray[count][1] = j;
                    sparseArray[count][2] = chessArray1[i][j];
                    count++;
                }
            }
        }
        //输出稀疏数组
        System.out.println("转换后的稀疏数组===》");
        for (int[] row : sparseArray) {
            System.out.printf("%d\t %d\t %d", row[0], row[1], row[2]);
            System.out.println();
        }


        //恢复稀疏数组
        // 1.先读取第一行
        int chessArray2[][] = new int[sparseArray[0][0]][sparseArray[0][1]];
        int summary = sparseArray[0][2];
        for (int i = 1; i <= summary; i++) {
            chessArray2[sparseArray[i][0]][sparseArray[i][1]] = sparseArray[i][2];
        }
        System.out.println("打印新的的二维数组");
        for (int[] row : chessArray2) {
            for (int data : row) {
                System.out.printf("%d\t", data);
            }
            System.out.println();
        }
    }
}
