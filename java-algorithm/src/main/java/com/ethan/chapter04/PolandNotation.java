package com.ethan.chapter04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * @className: PolandNotation
 * @author: Ethan
 * @date: 26/6/2021
 **/
public class PolandNotation {
    public static void main(String[] args) {
        //1.完成中缀表达式转后缀表达式 1+((2+3)*4)-5
        String express = "1+((2+3)*4)-5";
        List<String> expressList = toInfixExpressionList(express);
        List<String> suffixExpression = parseSuffixExpression(expressList);
        System.out.println(suffixExpression);

        // (3+4)*5-6 ==> 3 4 + 5 * 6 -
        // (30+4)*5-6 ==> 3 4 + 5 * 6 -
        // 4 * 5 - 8 + 60 + 8 / 2 ==> 4 5 * 8 - 60 + 8 2  /  +
        String expression = "30 4 + 5 x 6 -";
        // 1. 先将表达式放入ArrayList中
        // 2. 将ArrayList 配合栈,完成计算
        List<String> listString = getListString(expression);
        System.out.println(listString);
        int res = calculate(listString);
        System.out.println("计算的结果是:" + res);
    }

    /**
     * 中缀表达式转后缀表达式
     * @param expression
     * @return
     */
    public static List<String> parseSuffixExpression(List<String> expression) {
        // 1 2 3 + 4 * + 5 -
        Stack<String> s1 = new Stack<>();
        // s2在整个过程中没有pop操作,可以直接使用List来替代
        List<String> s2 = new ArrayList<>();
        for (String s : expression) {
            if (s.matches("\\d+")) {
                s2.add(s);
            } else if (s.equals("(")) {
                s1.push(s);
            } else if (s.equals(")")) {
                // 如果是 ) 依次弹出s1栈顶的运算符,并压入s2,直到遇到左括号为止,此时这对括号丢弃
                while (!s1.peek().equals("(")) {
                    s2.add(s1.pop());
                }
                s1.pop();
            } else {
                //若s的优先级小于等于栈顶运算符的有优先级,将s1运算符弹出并压入s2,
                // 再次与新的栈顶的运算符比较
                while (s1.size() != 0 && priority(s) <= priority(s1.peek())) {
                    s2.add(s1.pop());
                }
                //item压入栈中
                s1.push(s);
            }
        }
        while (s1.size() > 0) {
            s2.add(s1.pop());
        }
        return s2;
    }
    public static List<String> toInfixExpressionList(String expression) {
        List<String> arr = new ArrayList<>();
        int i = 0;
        String str = "";
        char c;
        while (i < expression.length()) {
            // 如果是非数字,加入列表中
            c = expression.charAt(i);
            if (!Character.isDigit(c)) {
                if (str.length() > 0) {
                    arr.add(str);
                }
                arr.add(c + "");
                str = "";
            } else {
                str += expression.charAt(i);
            }
            i++;
            //是否是最后一个index
            if (i == expression.length()) {
                arr.add(str);
            }
        }
        return arr;
    }
    public static List<String> getListString(String expression) {
        return Arrays.asList(expression.split(" "));
    }

    public static int priority(String s1) {
        switch (s1) {
            case "*" :
            case "/":
                return 2;
            case "+":
            case "-":
                return 1;
            default:
                return 0;
        }
    }

    //完成逆波兰表达式
    public static int calculate(List<String> ls) {
        Stack<String> stack = new Stack<>();
        for (String item : ls) {
            if (item.matches("\\d+")) {
                stack.push(item);
            } else {
                // pop两个数,运算再入栈
                int num1 = Integer.parseInt(stack.pop());
                int num2 = Integer.parseInt(stack.pop());
                int res = 0;
                switch (item) {
                    case "+":
                        res = num2 + num1;
                        break;
                    case "-":
                        res = num2 - num1;
                        break;
                    case "x":
                        res = num2 * num1;
                        break;
                    case "/":
                        res = num2 / num1;
                        break;
                    default:
                        throw new RuntimeException("运算发有误");
                }
                stack.push(String.valueOf(res));
            }
        }
        return Integer.parseInt(stack.pop());
    }

}
