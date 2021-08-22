package com.ethan.tree;

/**
 * @className: ArrayListDemo
 * @author: Ethan
 * @date: 12/7/2021
 *
 *   顺序遍历二叉树
 *
 *   需求：给你一个数组{1,2,3,4,5,6,7},要求以二叉树前序遍历的方式进行遍历
 *   前序遍历的结果为1,2,4,5,3,6,7
 *   中序遍历的结果为4,2,5,1,6,3,7
 *   后序遍历的结果为4,5,2,6,7,3,1
 **/
public class ArrayBinaryTreeDemo {
    public static void main(String[] args) {
        // 先创建一个二叉树
        int[] arr = {1, 2, 3, 4, 5, 6, 7};
        ArrayBinaryTree arrayBinaryTree = new ArrayBinaryTree(arr);
        arrayBinaryTree.preOrder();
        System.out.println("中序遍历");
        arrayBinaryTree.infixOrder(0);
        System.out.println("后序遍历");
        arrayBinaryTree.postOrder(0);
    }

    //构建类实现顺序存储二叉树
    static class ArrayBinaryTree {
        public int[] arr;

        public ArrayBinaryTree(int[] arr) {
            this.arr = arr;
        }

        //完成顺序存储二叉树的前序遍历
        public void preOrder() {
            preOrder(0);
        }

        /**
         * @param index 数组的下标
         */
        public void preOrder(int index) {
            //如果数组为空，或者arr.length = 0
            if (arr == null || arr.length == 0) {
                System.out.println("数组为空，不能按照二叉树的前序遍历");
            }
            //输出当前数组元素
            System.out.println(arr[index]);
            // 向左递归遍历
            int left = (2 * index) + 1;
            if (left < arr.length) {
                preOrder(left);
            }
            //向右递归
            int right = (2 * index) + 2;
            if (right < arr.length) {
                preOrder(right);
            }
        }

        //中序遍历
        public void infixOrder(int index) {
            //如果数组为空，或者arr.length = 0
            if (arr == null || arr.length == 0) {
                System.out.println("数组为空，不能按照二叉树的前序遍历");
            }
            // 向左递归遍历
            int left = (2 * index) + 1;
            if (left < arr.length) {
                infixOrder(left);
            }
            //输出当前数组元素
            System.out.println(arr[index]);
            //向右递归
            int right = (2 * index) + 2;
            if (right < arr.length) {
                infixOrder(right);
            }
        }

        public void postOrder(int index) {
            //如果数组为空，或者arr.length = 0
            if (arr == null || arr.length == 0) {
                System.out.println("数组为空，不能按照二叉树的前序遍历");
            }
            // 向左递归遍历
            int left = (2 * index) + 1;
            if (left < arr.length) {
                postOrder(left);
            }
            //向右递归
            int right = (2 * index) + 2;
            if (right < arr.length) {
                postOrder(right);
            }
            //输出当前数组元素
            System.out.println(arr[index]);
        }

        public void delNode(int no) {
            // if (this.root != null) {
            //     //这里需要判断root是否是要删除的节点
            //     if (root.no == no) {
            //         this.root = null;
            //     } else {
            //         this.root.delNode(no);
            //     }
            // } else {
            //     System.out.println("二叉树为空，无法删除");
            // }
        }

    }
}

