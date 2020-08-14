package com.ethan.test.algorithm;

public class TwoNumberMultiply {
  public String multiply(String num1, String num2) {
//         123
//       x 456
//          1 5
//        1 0
//      0 5
//    --------------
//        1 2
//      0 8
//    0 4
    if ("0".equals(num1) || "0".equals(num2)) {
      return "0";
    }
    int[] result = new int[num1.length() + num2.length()];
    for (int j = 0; j < num2.length(); j++) {
      int num2Char = num2.charAt(j) - '0';
      for (int i = 0; i < num1.length(); i++) {
        int num1Char = num1.charAt(i) - '0';
        int multiplyVal = num1Char * num2Char;
        if (multiplyVal < 10) {
          result[i + j + 1] += multiplyVal;
        } else {
          result[i + j] += multiplyVal / 10;
          result[i + j + 1] += multiplyVal % 10;
        }
      }
    }

    for (int i = result.length - 1; i >= 0; i--) {
      int singlePlace = result[i] % 10;
      int tenPlace = result[i] / 10;
      result[i] = singlePlace;
      if (i>0) {
        result[i - 1] += tenPlace;
      }
    }
    StringBuilder sb = new StringBuilder();
    for (int r = 0; r < result.length; r++) {
      if (r == 0 && result[r] == 0) {
        continue;
      }
      sb.append(result[r]);
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    TwoNumberMultiply t = new TwoNumberMultiply();
    System.out.println(t.multiply("123", "456"));
  }
}
