package com.ethan.hashtab;

/**
 * @className: HashTabDemo
 * @author: Ethan
 * @date: 12/7/2021
 **/
public class HashTabDemo {
    public static void main(String[] args) {
        HashTab hashTab = new HashTab(7);
        hashTab.add(new Emp(1, "John"));
        hashTab.add(new Emp(2, "John2"));
        hashTab.add(new Emp(3, "John3"));
        hashTab.add(new Emp(4, "John4"));
        hashTab.add(new Emp(5, "John5"));
        hashTab.add(new Emp(6, "John6"));
        hashTab.add(new Emp(7, "John7"));
        hashTab.add(new Emp(8, "John8"));
        hashTab.list();
        System.out.println("===>find by id");
        hashTab.findEmpById(8);
        hashTab.findEmpById(9);
    }

    // 创建HashTable，管理多条链表
    static class HashTab {
        private EmpLinkedList[] empLinkedListArr;
        private int size;
        public HashTab(int size) {
            this.empLinkedListArr = new EmpLinkedList[size];
            this.size = size;
            //分别初始化每条链表
            for (int i = 0; i < empLinkedListArr.length; i++) {
                empLinkedListArr[i] = new EmpLinkedList();
            }
        }

        // 添加雇员
        public void add(Emp emp) {
            //根据员工的ID得到该员工添加到哪条链表
            int empLinkedListIdx = hashFun(emp.id);
            //将emp添加到对应的链表
            empLinkedListArr[empLinkedListIdx].add(emp);
        }
        //遍历所有的链表
        public void list() {
            for (int i = 0; i < size; i++) {
                System.out.println("====> linkedList Index:" + i);
                empLinkedListArr[i].list();
            }
        }

        //根据输入的id查找雇员
        public void findEmpById(int id) {

            int empLinkedListIdx = hashFun(id);
            //将emp添加到对应的链表
            Emp emp = empLinkedListArr[empLinkedListIdx].findEmpById(id);
            if (emp != null) {
                System.out.printf("在第%d链表中找到雇员%s", empLinkedListIdx, emp);
                System.out.println();
            } else {
                System.out.println("在哈希表中，没有找到该雇员");
                System.out.println();
            }
        }

        //编写散列函数，使用简单的取模法
        public int hashFun(int id) {
            return id % size;
        }
    }

    //EmpLinkedList 表示链表
    static class EmpLinkedList {
        // 头指针指向第一个雇员，我们这个链表的head是直接指向第一个Emp
        private Emp head;

        //添加雇员到链表
        // 1. 假定添加雇员时，id是自增长，即id的分配总是从小到大
        //因此我们将雇员直接加入到链表最后即可
        public void add(Emp emp) {
            if (head == null) {
                head = emp;
                return;
            }
            //如果不是第一个，使用辅助指针
            Emp cur = head;
            while (cur.next != null) {
                cur = cur.next;
            }
            //退出时直接加入链表
            cur.next = emp;
        }

        //遍历链表雇员信息
        public void list() {
            if (head == null) {
                System.out.println("当前链表为空");
                return;
            }
            Emp cur = head;
            while (true) {
                System.out.println(cur);
                if (cur.next == null) {
                    break;
                }
                cur = cur.next;
            }
        }

        //根据id查找雇员
        // 如果找到就返回Emp,如果没找到就返回null
        public Emp findEmpById(int id) {
            if (head == null) {
                System.out.println("链表为空");
                return null;
            }
            Emp cur = head;
            while (true) {
                if (cur.id == id) {
                    break;
                }
                if (cur.next == null) {
                    cur = null;
                    break;
                }
                cur = cur.next;
            }
            return cur;
        }
    }

    //雇员
    static class Emp {
        public int id;
        public String name;
        public Emp next;

        public Emp(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return "Emp{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
