package com.ethan.chapter04;

import java.util.Scanner;
import java.util.Stack;

/**
 * @className: StackDemo
 * @author: Ethan
 * @date: 24/6/2021
 *
 * 用栈完成计算一个表达式的结果
 * 7*2*2-5+1-5+3-4=？
 *
 * 思路：
 * 1. 通过一个index来遍历表达式
 * 2. 如果是一个数字，放入数字栈
 * 3. 如果是一个符号
 *  3.1 如果当前符号栈是空，直接入栈
 *  3.2 如果符号栈有操作符
 *      3.2.1 如果当前操作符的优先级小于或者等于 栈中的操作符，就需要从数栈中pop出两个数，再从符号栈中pop一个符号运算，
 *          将结果再入数栈 + 将当前操作符入符号栈
 *      3.2.2 如果优先级 大于 栈中的操作符，直接入符号
 * 4. 扫面完毕，就顺序从数栈和符号栈中pop出响应的数和符号并运算
 * 5. 最后数栈只有一个数字，就是表达式的结果
 *
 * 7 2  ==> 14 ==》 28
 * *    ==> *  ==》 -
 **/
public class StackCalculatorDemo {
    public static void main(String[] args) {
        // Stack<Integer> stack = new Stack<>();
        // stack.peek();

        String expression = "70*2*2-5+1-5+3-4";
        // String expression = "70*2*2";
        char[] chars = expression.toCharArray();
        ArrayStack2 numberStack = new ArrayStack2(chars.length);
        ArrayStack2 operatorStack = new ArrayStack2(chars.length);
        int num1 = 0;
        int num2 = 0;
        int operator = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            boolean operation = isOperation(aChar);
            if (operation) {
                if (!operatorStack.isEmpty()) {
                    //处理
                    int top = operatorStack.peek();
 //                    如果当前操作符的优先级小于或者等于 栈中的操作符，就需要从数栈中pop出两个数，再从符号栈中pop一个符号运算，
 //                   将结果再入数栈 + 将当前操作符入符号栈
                    if (priority(aChar) <= priority(top)) {
                        num1 = numberStack.pop();
                        num2 = numberStack.pop();
                        operator = operatorStack.pop();
                        int result = calculator(num1, num2, operator);
                        //把结果和最新的符号入栈
                        numberStack.push(result);
                        operatorStack.push(aChar);
                    } else {
                        // 如果优先级 大于 栈中的操作符，直接入符号
                        operatorStack.push(aChar);
                    }
                } else{
                    //直接入栈
                    operatorStack.push(aChar);
                }

            } else {//如果是数字，直接入栈

                if (i + 1 < chars.length) {
                    stringBuilder.append(aChar);
                    if (isOperation(chars[i + 1])) {
                        numberStack.push(Integer.parseInt(stringBuilder.toString()));
                        stringBuilder.setLength(0);
                    }
                } else {
                    numberStack.push(Character.getNumericValue(aChar));
                }
            }
        }
        //扫面完毕，就顺序从数栈和符号栈中pop出响应的数和符号并运算
        while (true) {
            // 如果符号栈为空，表示数栈中只有一个结果
            if (operatorStack.isEmpty()) {
                break;
            }
            num1 = numberStack.pop();
            num2 = numberStack.pop();
            operator = operatorStack.pop();
            int result = calculator(num1, num2, operator);
            //把结果和最新的符号入栈
            numberStack.push(result);
        }

        System.out.println("结果是："+numberStack.pop());
    }

    //返回运算符的优先级，返回数字越大，则优先级越高
    // 假定目前表达式只有+ - * /
    public static int priority(int operator) {
        if (operator == '*' || operator == '/') {
            return 1;
        } else if (operator == '+' || operator == '-') {
            return 0;
        } else {
            return -1;
        }
    }

    //判断是不是一个运算符
    public static boolean isOperation(char val) {
        return val == '+' || val == '-' || val == '*' || val == '-';
    }

    //计算方法
    public static int calculator(int num1, int num2, int operator) {
        int res = 0;
        switch (operator) {
            case '+':
                res = num1 + num2;
                break;
            case '-':
                //顺序需要根据顺序
                res = num2 - num1;
                break;
            case '*':
                res = num2 * num1;
                break;
            case '/':
                res = num2 / num1;
                break;
            default:
                break;
        }
        return res;
    }
}

//定义一个栈
class ArrayStack2 {
    private int maxSize;
    private int[] stack;//数组模拟栈，数据就放在这里
    private int top = -1; //top表示栈顶

    public ArrayStack2(int maxSize) {
        this.maxSize = maxSize;
        stack = new int[maxSize];
    }

    //栈满
    public boolean isFull() {
        return top == maxSize - 1;
    }
    //栈空
    public boolean isEmpty() {
        return top == -1;
    }

    //入栈
    public void push(int val) {
        //先判断栈是否满
        if (isFull()) {
            System.out.println("栈满");
            return;
        }
        top++;
        stack[top] = val;
    }
    //出栈
    public int pop() {
        if (isEmpty()) {
            throw new RuntimeException("栈空");
        }
        int val = stack[top];
        top--;
        return val;
    }
    //返回栈顶的值
    public int peek() {
        return stack[top];
    }

    //遍历栈
    public void list() {
        if (isEmpty()) {
            System.out.println("栈空");
            return;
        }
        for (int i = top; i <= 0; i--) {
            System.out.printf("stack[%d]=%d\n", i, stack[i]);
        }
    }
}
