package com.ethan.algorithm;

import java.util.Arrays;

/**
 * @className: KMPAlgorithm
 * @author: Ethan
 * @date: 9/9/2021
 **/
public class KMPAlgorithm {
    public static void main(String[] args) {
        String str1 = "BBC ABCDAB ABCDABCDABDE";
        String str2 = "ABCDABD";
        //AAAB  【0,1,2,0】
        int[] next = kmpNext(str2);
        System.out.println(Arrays.toString(next));

        // System.out.println(kmpSearch("BB BBC ABCD", "BBC", next));
        System.out.println(kmpSearch(str1, str2, next));
    }

    //写出KMP搜索算法, 如果是-1就是没有匹配到，否则返回第一个匹配的位置
    public static int kmpSearch(String str1, String str2, int[] next) {
        for (int i = 0, j = 0; i < str1.length(); i++) {

            //需要处理str1.charAt(i) != str2.charAt(j)
            //KMP算法核心
            while (j > 0 && str1.charAt(i) != str2.charAt(j)) {
                j = next[j - 1];
            }

            if (str1.charAt(i) == str2.charAt(j)) {
                j++;
            }
            if (j == str2.length()) {
                return i - j + 1;
            }
        }
        return -1;
    }

    //获取到一个字符串（子串）的部分匹配值表
    public static int[] kmpNext(String dest) {
    //    创建一个next数组保存部分匹配值
        int[] next = new int[dest.length()];
        next[0] = 0;//如果字符串长度为1，部分匹配值为0
        for (int i = 1, j = 0; i < dest.length(); i++) {
            //当dest.charAt(i) != dest.charAt(j)时,我们需要从next[j-1]获取新的j
            // 直到我们发现有dest.charAt(i) == dest.charAt(j)成立才退出
            //这是KMP算法的核心点
            while (j > 0 && dest.charAt(i) != dest.charAt(j)) {
                j = next[j - 1];
            }
            //当dest.charAt(i) == dest.charAt(j)满足时,部分匹配值就是+1
            if (dest.charAt(i) == dest.charAt(j)) {
                //当条件满足时，部分匹配值+1
                j++;
            }
            next[i] = j;
        }
        return next;
    }

}
