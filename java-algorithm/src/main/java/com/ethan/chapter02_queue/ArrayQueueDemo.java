package com.ethan.chapter02_queue;

import java.util.Scanner;

/**
 * @className: ArrayQueue
 * @author: Ethan
 * @date: 20/6/2021
 **/
public class ArrayQueueDemo {
    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue(3);

        Scanner scanner = new Scanner(System.in);
        boolean loop = true;
        char key;
        while (loop) {
            System.out.println("s(show):显式队列");
            System.out.println("e(exit):退出程序");
            System.out.println("a(add):添加数据");
            System.out.println("g(get):从队列取出数据");
            System.out.println("h(head):查看队列头数据");
            key = scanner.next().charAt(0);
            switch (key) {
                case 's':
                    queue.showQueue();
                    break;
                case 'a':
                    System.out.println("输出一个数字");
                    int value = scanner.nextInt();
                    queue.addQueue(value);
                    break;
                case 'e':
                    scanner.close();
                    loop = false;
                    break;
                case 'g':
                    try {
                        int data = queue.getQueue();
                        System.out.printf("取出的数据是%d\n", data);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case 'h':
                    try {
                        int data = queue.headQueue();
                        System.out.printf("队列的头数据是%d\n", data);
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

class ArrayQueue {
    private int maxSize;
    private int head;
    private int tail;
    private int[] arr;
    //创建队列的构造器

    public ArrayQueue(int maxSize) {
        this.maxSize = maxSize + 1;
        arr = new int[maxSize + 1];
        head = 0; //指向队列头部
        tail = 0;  //指向队列尾部
    }

    //判断队列是否已满
    // 因为预留了一个空间，实际size是 maxSize -1
    public boolean isFull() {
        return (tail + 1) % maxSize == head;
    }

    //判断是否为空
    public boolean isEmpty() {
        return head == tail;
    }

    //添加数据队列

    /**
     * 使用算法改造成环形队列
     * 1. front变量的含义做一个调整：front就指向队列的第一个元素，也就是说arr[front]就是队列的第一个元素.front的初始值=0
     * 2. tail变量的含义做一个调整：tail指向队列的最后一个元素的后一个位置．因为希望空出一个空间做为约定. tail的初始值=0
     * 3. 当队列满时，条件是(tail+1)％maxSize == front【满】
     * 4. 对队列为仝的条件，tail==front【空】
     * 5. 当我们这样分析，队列中有效的据的个 (tail+maxSize-front)％maxSize //tail=1front=0
     * 6. 我们就可以在原来的队列上修改得到，一个环形队列
     * @param n
     */
    public void addQueue(int n) {
        if (isFull()) {
            System.out.println("队列已满，不能加入数据");
            return;
        }
        //直接加入
        arr[tail] = n;
        tail = (tail + 1) % maxSize;
    }

    //获取数据出队列
    public int getQueue() {
        if (isEmpty()) {
            System.out.println("队列为空，不能获取数据");
            throw new RuntimeException("队列为空，不能获取数据");
        }
        // 1. 这里需要分析出head是指向队列的第一个元素
        // 2. 将head后移
        // 3. 将临时保存的变量返回
        int value =  arr[head];
        head = (head + 1) % maxSize;
        return value;
    }

    //显式队列所有数据
    public void showQueue() {
        if (isEmpty()) {
            System.out.println("队列为空，不能获取数据");
            return;
        }
        // 从head开始遍历，遍历多少个元素

        for (int i = head; i < head + size(); i++) {
            System.out.printf("arr[%d]=%d \t", i % maxSize, arr[i % maxSize]);
        }
    }

    public int size() {
        return (tail + maxSize - head) % maxSize;
    }
    //显式队列的头部，注意不是取出数据
    public int headQueue() {
        if (isEmpty()) {
            System.out.println("队列为空，不能获取数据");
            throw new RuntimeException("队列为空，不能获取数据");
        }
        return arr[head];
    }
}