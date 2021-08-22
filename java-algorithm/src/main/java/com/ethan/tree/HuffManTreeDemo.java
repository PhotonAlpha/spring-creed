package com.ethan.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @className: HuffManTreeDemo
 * @author: Ethan
 * @date: 19/7/2021
 **/
public class HuffManTreeDemo {
    public static void main(String[] args) {
        int[] arr = {13, 7, 8, 3, 29, 6, 1};
        Node huffManTree = createHuffManTree(arr);

        //查看值
        //{67, 29, 38, 15,7,8,23,10,4,1,3,6,13}
        preOrder(huffManTree);
    }

    public static void preOrder(Node root) {
        if (root != null) {
            root.preOrder(root);
        } else {
            System.out.println("空树");
        }
    }

    //创建赫夫曼树的方法
    /**
     * @param arr
     * @return 返回创建好的赫夫曼树的节点
     */
    public static Node createHuffManTree(int[] arr) {
        // 1. 为了操作方便，遍历 arr数组
        // 2.将arr的每个元素构建成一个node
        // 3.将node放入ArrayList中
        List<Node> nodes = new ArrayList<>();
        for (int i : arr) {
            nodes.add(new Node(i));
        }
        //循环处理***********************
        while (nodes.size() > 1) {

            // 排序从小打到
            Collections.sort(nodes);

            System.out.println("nodes" + nodes);
            //1.取出权值最小的二叉树
            Node leftNode = nodes.get(0);
            //2.取出权值第二小的二叉树
            Node rightNode = nodes.get(1);
            //3. 构建一颗新的二叉树
            Node parent = new Node(leftNode.value + rightNode.value);
            parent.left = leftNode;
            parent.right = rightNode;
            //4.从arrList中删除处理过的二叉树
            nodes.remove(leftNode);
            nodes.remove(rightNode);
            //5.将parent加入到nodes
            nodes.add(parent);
        }
        //循环处理***********************
        // 返回Root节点
        return nodes.get(0);
    }

    //创建节点类
    //为了让node支持Collections排序，
    static class Node implements Comparable<Node> {
        int value;//节点权值
        Node left;//指向左子节点
        Node right;//指向右子节点

        public Node(int value) {
            this.value = value;
        }

        public void preOrder(Node root) {
            System.out.println(root);
            if (root.left != null) {
                preOrder(root.left);
            }
            if (root.right != null) {
                preOrder(root.right);
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    '}';
        }

        @Override
        public int compareTo(Node o) {
            //表示从小到大排序
            return this.value - o.value;
        }
    }
}
