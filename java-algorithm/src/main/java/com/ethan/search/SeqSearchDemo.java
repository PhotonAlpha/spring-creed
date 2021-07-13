package com.ethan.search;

/**
 * @className: SeqSearch
 * @author: Ethan
 * @date: 10/7/2021
 **/
public class SeqSearchDemo {
    public static void main(String[] args) {
        int[] arr = {1, 9, 11, -1, 34, 89};
        int value = 11;
        seqSearch(arr, value);

    }

    private static int seqSearch(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) {
                return i;
            }
        }
        return -1;
    }
}
