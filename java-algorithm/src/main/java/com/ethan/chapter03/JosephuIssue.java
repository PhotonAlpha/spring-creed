package com.ethan.chapter03;

/**
 * Josephu(约瑟夫、约瑟夫环）问题
 * Josephu问题力：设编号为1，2，…n的n个人围坐一圈，
 * 约定编号为k（1<=k<=n）的人从1开始报数，数到m的那个
 * 人出列，它的下一位又从1开始报数，数到m的那个人又出
 * 列，依次类推，直到所有人出列为止，由此产生一个出队
 * 编号的序列。
 *
 * 提示：用一个不带头结点的循环链表来处里Josephu问题．
 * 先成一个有n个结点的单循环链表，然后由点起从1开
 * 始计数，计到m时，对应结点从链表中删除，然后再从
 * 删除结点的下一个黠点又从1开始计数，直到最后一个结点
 * 从链表中删除结束。
 *
 * n=5 总人数
 * k=1 第几个开始报数
 * m=2 数几下
 * 出队列顺序2-》4-》1-》5-》3
 *
 * @className: JosephuIssue
 * @author: Ethan
 * @date: 23/6/2021
 **/
public class JosephuIssue {
    public static void main(String[] args) {
        CircleSingleLinkedList circleSingleLinkedList = new CircleSingleLinkedList();
        // circleSingleLinkedList.addNode(5);
        // circleSingleLinkedList.list();
        circleSingleLinkedList.countPop(1, 2, 25);
    }
}

//创建环形单向链表
class CircleSingleLinkedList {
    private Node head = new Node(-1);//创建默认值

    //根据用户输入，计算出出圈的顺序
    public void countPop(int startNo, int step, int nums) {
        //先对数据进行校验
        if (head == null || startNo < 1 || startNo > nums || step > nums) {
            System.out.println("参数输入有误，请重新输入");
        }
        //创建复制指针
        addNode(nums);

        Node pre = head;
        while (true) {
            if (pre.next == head) { // 让pre指向最后一个元素
                break;
            }
            // 设置值
            pre = pre.next;
        }
        //先定位
        for (int i = 1; i < startNo; i++) {
            head = head.next;
            pre = pre.next;
        }
        System.out.println("head:" + head + " pre:" + pre);

        while (true) {
            if (pre == head) {
                //说明只剩下一个节点
                break;
            }
            //让 pre 开始报数，移动 step 次
            for (int i = 1; i < step; i++) {
                head = head.next;
                pre = pre.next;
            }
            //此时head就是需要移出的节点
            System.out.println("出圈节点：" + head);
            head = head.next;
            pre.next = head;
        }
        System.out.println("最后剩下的编号是：" + head);
    }

    //添加节点，构建环形链表
    public void addNode(int nums) {
        if (nums < 1) {
            System.out.println("nums不能小于1");
            return;
        }
        // 创建环形链表;
        Node cur = null;
        for (int i = 1; i <= nums; i++) {
            //根据编号创建子节点
            Node node = new Node(i);
            if (i == 1) {
                head = node;
                head.next = node;
                cur = node;
            } else {
                cur.next = node;
                node.next = head;

                cur = node;
            }
        }
    }

    public void list() {
        //判断链表是否为空
        if (head == null || head.next == null) {
            System.out.println("链表为空");
            return;
        }
        Node cur = head;
        while (true) {
            System.out.println(cur);
            if (cur.next.value == head.value) {
                break;
            }
            cur = cur.next;
        }
    }
}

class Node {
    public int value;
    public Node next;

    public Node(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Node{" +
                "value=" + value +
                '}';
    }
}
