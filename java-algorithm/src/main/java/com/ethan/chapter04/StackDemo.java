package com.ethan.chapter04;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

/**
 * @className: StackDemo
 * @author: Ethan
 * @date: 24/6/2021
 **/
public class StackDemo {
    public static void main(String[] args) {
        // Stack
        // ArrayList<String> arrayList = new ArrayList<>();
        //
        // LinkedList<String> linkedList = new LinkedList<>();
        // linkedList.pop();
        ArrayStack stack = new ArrayStack(4);

        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        char key;
        while (loop) {
            System.out.println("s(show):显式栈");
            System.out.println("e(exit):退出程序");
            System.out.println("a(add):添加数据");
            System.out.println("p(pop):取出数据");
            key = scanner.next().charAt(0);
            switch (key) {
                case 's':
                    stack.list();
                    break;
                case 'a':
                    System.out.println("输出一个数字");
                    int value = scanner.nextInt();
                    stack.push(value);
                    break;
                case 'e':
                    scanner.close();
                    loop = false;
                    break;
                case 'p':
                    try {
                        int data = stack.pop();
                        System.out.printf("取出的数据是%d\n", data);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                default:
                    break;
            }
        }
        System.out.println("程序退出");
    }
}

//定义一个栈
class ArrayStack {
    private int maxSize;
    private int[] stack;//数组模拟栈，数据就放在这里
    private int top = -1; //top表示栈顶

    public ArrayStack(int maxSize) {
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