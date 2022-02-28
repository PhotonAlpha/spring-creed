package com.ethan.algorithm;

/**
 * @className: ViolenceMatch
 * @author: Ethan
 * @date: 8/9/2021
 *
 * 暴力匹配算法
 **/
public class ViolenceMatch {
    public static void main(String[] args) {
        String str1 = "你你大厦健康的萨达撒";
        String str2 = "健康的2";
        System.out.println(violenceMatch(str1, str2));
    }

    // 暴力匹配算法
    public static int violenceMatch(String str1, String str2) {
        char[] s1 = str1.toCharArray();
        char[] s2 = str2.toCharArray();

        int s1Len = s1.length;
        int s2Len = s2.length;

        int i = 0;
        int j = 0;
        while (i < s1Len && j < s2Len) { //保证匹配时不越界
            if (s1[i] == s2[j]) {
                i++;
                j++;
            } else {
                //没有匹配成功
                i = i - (j - 1);
                j = 0;
            }
        }
        if (j == s2Len) {
            return i - j;
        }else {
            return -1;
        }
    }
}
