package com.ethan.chapter03;

import java.util.Stack;

/**
 * @className: LinkedList
 * @author: Ethan
 * @date: 21/6/2021
 **/
public class LinkedListDemo {
    public static void main(String[] args) {
        SingleLinkedList linkedList = new SingleLinkedList();
        linkedList.addByOrder(new HeroNode(1, "宋江", "及时雨"));
        linkedList.addByOrder(new HeroNode(4, "林冲", "爆炸头"));
        linkedList.addByOrder(new HeroNode(2, "卢俊义", "玉麒麟"));
        linkedList.addByOrder(new HeroNode(3, "吴用", "智多星"));

        linkedList.list();
        linkedList.update(new HeroNode(4, "林冲", "豹子头"));
        System.out.println("-0-");
        linkedList.list();

        System.out.println(getLength(linkedList.getHead()));
        System.out.println(findLatIndex(linkedList.getHead(), 3));
        System.out.println("开始反转");
        // 单链表的反转！！！
        revert(linkedList.getHead());
        linkedList.list();
        System.out.println("逆序打印");
        revertPrint(linkedList.getHead());
    }

    /**
     * 单链表的反转！！！
     *
     * @param heroNode
     * @return
     */
    public static void revert(HeroNode heroNode) {
        if (heroNode.next == null || heroNode.next.next == null) {
            return;
        }
        HeroNode revertHead = new HeroNode(0, "", "");
        //定义一个辅助指针
        HeroNode cur = heroNode.next;
        HeroNode next = null; //指向当前节点的下一个节点
        while (cur != null) {
            next = cur.next;
            cur.next = revertHead.next;
            revertHead.next = cur;
            cur = next; // 让cur后移
        }
        //将head.next指向revertHead.next
        heroNode.next = revertHead.next;
    }

    /**
     * 逆序打印
     * ！！可以利用栈的数据结构，将各个节点压入到栈中，然后利用栈先进后出的特点，实现逆序打印的效果
     *
     * @return
     */
    public static void revertPrint(HeroNode head) {
        if (head == null || head.next == null) {
            System.out.println("链表为空");
            return;
        }
        Stack<HeroNode> stack = new Stack<>();
        HeroNode cur = head.next;
        while (cur != null) {
            stack.push(cur);
            cur = cur.next;
        }
        while (stack.size() > 0) {
            HeroNode pop = stack.pop();//pop就是出栈的操作
            System.out.println(pop);
        }

    }


    //获取单链表倒数第K个节点
    // 1.先获取单链表长度
    // 2. 总长度 - index 即是该节点
    public static HeroNode findLatIndex(HeroNode heroNode, int index) {
        if (heroNode.next == null) {
            return null;
        }
        int size = getLength(heroNode);
        if (index <= 0 || index > size) {
            return null;
        }
        HeroNode cur = heroNode.next;
        int count = size - index;
        while (true) {
            if (count <= 0) {
                return cur;
            }
            cur = cur.next;
            count--;
        }
    }


    //获取单链表有效节点的个数（如果带头结点，需要不统计头结点）
    public static int getLength(HeroNode heroNode) {
        int count = 0;

        if (heroNode.next == null) {
            return count;
        }
        HeroNode cur = heroNode.next;
        while (cur != null) {
            count++;
            cur = cur.next;
        }
        return count;
    }
}

class SingleLinkedList {
    // 先初始化一个头结点，虚节点
    private HeroNode head = new HeroNode(0, "", "");

    public HeroNode getHead() {
        return head;
    }
    //添加节点到单向链表
    /**
     * 当不考虑编号的顺序时
     * 1. 找到当前链表的最后节点
     * 2. 将最后的这个节点的next指向新的节点
     * @param heroNode
     */
    public void add(HeroNode heroNode) {
        HeroNode temp = head;
        while (true) {
            //找到链表的最后
            if (temp.next == null) {
                break;
            }
            //如果没有找到，将temp后移
            temp = temp.next;
        }
        //当退出循环时，temp就是链表的最后
        temp.next = heroNode;
    }

    /**
     * 当考虑编号的顺序时,如果有排名，就添加失败
     * 1. 找到新添加的节点的位置
     * 2. 新的节点的.next = temp.next
     * 3. temp.next = 新的节点
     * @param heroNode
     */
    public void addByOrder(HeroNode heroNode) {
        HeroNode temp = head;
        boolean flag = false; // 查看添加的编号是否存在，默认为false
        while (true) {
            //找到链表添加位置的前一个节点
            if (temp.next == null) {//说明已经在链表最后，直接添加
                //当退出循环时，temp就是链表的最后
                break;
            }
            if (temp.next.no > heroNode.no) { // 在temp后面添加
                break;
            } else if (temp.next.no == heroNode.no) {
                flag = true;//说明编号存在
                break;
            }
            temp = temp.next;
        }
        if (flag) { //不能添加
            System.out.println("数据已经存在，无法添加。");
        } else {
            //添加到链表中
            heroNode.next = temp.next;
            temp.next = heroNode;
        }
    }

    //修改节点的信息，根据no来修改
    public void update(HeroNode heroNode) {
        if (head.next == null) {
            System.out.println("链表为空");
            return;
        }
        HeroNode temp = head.next;
        while (true) {
            if (temp.next == null) {
                break;
            } else if (temp.next.no == heroNode.no) {
                temp.next = heroNode;
                heroNode.next = temp.next.next;
                break;
            }
            //将next后移
            temp = temp.next;
        }
    }

    //显式链表
    public void list() {
        if (head.next == null) {
            System.out.println("链表为空");
            return;
        }
        HeroNode temp = head.next;
        while (true) {
            //输出节点的信息
            System.out.println(temp);
            if (temp.next == null) {
                break;
            }
            //将next后移
            temp = temp.next;
        }
    }
}

class HeroNode {
    public int no;
    public String name;
    public String nickName;
    public HeroNode next;

    public HeroNode(int no, String name, String nickName) {
        this.no = no;
        this.name = name;
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "HeroNode{" +
                "no=" + no +
                ", name='" + name + '\'' +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
