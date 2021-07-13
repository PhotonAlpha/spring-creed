package com.ethan.chapter03;

/**
 * @className: DoubleLinkedList
 * @author: Ethan
 * @date: 23/6/2021
 **/
public class DoubleLinkedListDemo {
    public static void main(String[] args) {
        DoubleLinkedList linkedList = new DoubleLinkedList();
        linkedList.add(new HeroNode2(1, "宋江", "及时雨"));
        linkedList.add(new HeroNode2(4, "林冲", "爆炸头"));
        linkedList.add(new HeroNode2(2, "卢俊义", "玉麒麟"));
        linkedList.add(new HeroNode2(3, "吴用", "智多星"));
        linkedList.add(new HeroNode2(5, "关胜", "虬须公"));

        linkedList.list();
        linkedList.update(new HeroNode2(4, "林冲", "豹子头"));
        System.out.println("-0-");
        linkedList.list();
        System.out.println("开始删除");
        linkedList.del(5);
        linkedList.list();
        // System.out.println(getLength(linkedList.getHead()));
        // System.out.println(findLatIndex(linkedList.getHead(), 3));
        // System.out.println("开始反转");
        // // 单链表的反转！！！
        // revert(linkedList.getHead());
        // linkedList.list();
        // System.out.println("逆序打印");
        // revertPrint(linkedList.getHead());

    }
}


class DoubleLinkedList {
    // 先初始化一个头结点，虚节点
    private HeroNode2 head = new HeroNode2(0, "", "");

    public HeroNode2 getHead() {
        return head;
    }
    //添加节点到单向链表
    /**
     * 当不考虑编号的顺序时
     * 1. 找到当前链表的最后节点
     * 2. 将最后的这个节点的next指向新的节点
     * @param heroNode
     */
    public void add(HeroNode2 heroNode) {
        HeroNode2 temp = head;
        while (true) {
            //找到链表的最后
            if (temp.next == null) {
                break;
            }
            //如果没有找到，将temp后移
            temp = temp.next;
        }
        //当退出循环时，temp就是链表的最后
        // 形成双向链表
        temp.next = heroNode;
        heroNode.pre = temp;
    }

    /**
     * 当考虑编号的顺序时,如果有排名，就添加失败
     * 1. 找到新添加的节点的位置
     * 2. 新的节点的.next = temp.next
     * 3. temp.next = 新的节点
     * @param heroNode
     */
    /*public void addByOrder(HeroNode heroNode) {
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
    }*/

    //修改节点的信息，根据no来修改
    public void update(HeroNode2 heroNode) {
        if (head.next == null) {
            System.out.println("链表为空");
            return;
        }
        HeroNode2 temp = head.next;
        while (true) {
            if (temp.next == null) {
                break;
            } else if (temp.no == heroNode.no) {
                heroNode.next = temp.next;
                heroNode.pre = temp.pre;
                temp.pre.next = heroNode;
                temp.next.pre = heroNode;
                temp.next = null;
                temp.pre = null;
                break;
            }
            //将next后移
            temp = temp.next;
        }
    }

    /**
     * 双向链表可以直接找到自我节点，然后删除
     * 不需要找前一个节点
     * @param no
     */
    public void del(int no) {
        HeroNode2 temp = head;
        if (head.next == null) {
            System.out.println("链表为空，无法删除");
            return;
        }
        boolean flag = false;
        while (true) {
            if (temp.no == no) {
                flag = true;
                break;
            }
            if (temp.next == null) {
                break;
            }
            temp = temp.next;
        }
        if (flag) {
            //删除
            temp.pre.next = temp.next;
            if (temp.next != null) {
                temp.next.pre = temp.pre;
            }
        }
    }

    //显式链表
    public void list() {
        if (head.next == null) {
            System.out.println("链表为空");
            return;
        }
        HeroNode2 temp = head.next;
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

//创建双向链表的类
class HeroNode2 {
    public int no;
    public String name;
    public String nickName;
    public HeroNode2 next;
    public HeroNode2 pre;

    public HeroNode2(int no, String name, String nickName) {
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
