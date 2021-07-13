package com.ethan.tree;

import java.util.ArrayList;

/**
 * @className: ArrayListDemo
 * @author: Ethan
 * @date: 12/7/2021
 *
 *      1.宋江
 *      /   \
 *     /     \
 *    /       \
 *   2.吴用    3.卢俊义
 *             / \
 *            /  \
 *           /    \
 *         5.关胜  4.林冲
 *
 *  前序 1 2 3 5 4
 *  中序 2 1 5 3 4
 *  后序 2 5 4 3 1
 *
 * 前序查找逻辑
 * 1.先判断当前结点的no否等于要查找的
 * 2，如果是相等，则返回当前结点
 * 3．如果不等，则判断当前结点的左子节点是否为空，如果不为空，
 *  则递归前序查找
 * 4．如果左递归前序查找，找到结点，则返回，否则续判断，当前的
 *  结点的右子节点是否为空，如果不空．则继续向右递归前序查找。
 *
 *  中序查找逻辑
 *  1.先判断当前结点的no左子节点是否为空，如果不为空递归中序查找
 *  2，如果是找到，则返回，如果没找到，就和当前节点比较，如果是则返回当前节点
 *      否则继续进行右递归的中序查找
 *  3．如果右递归中序查找，找到结点，则返回，否则返回空
 *
 *  后序查找逻辑
 *  1.先判断当前结点的no左子节点是否为空，如果不为空递归后序查找
 *  2，如果是相等，则返回当前结点。如果没找到，就判断当前节点的右子节点是否为空
 *      如果不为空，则右递归进行后续查找，如果找到就返回
 *  3．未找到就和当前节点比较，如果找到则返回，如果没找到则返回null。
 *
 **/
public class BinaryTreeDemo {
    public static void main(String[] args) {
        // 先创建一个二叉树
        BinaryTree binaryTree = new BinaryTree();
        HeroNode root = new HeroNode(1, "宋江");
        HeroNode node2 = new HeroNode(2, "吴用");
        HeroNode node3 = new HeroNode(3, "卢俊义");
        HeroNode node4 = new HeroNode(4, "林冲");
        HeroNode node5 = new HeroNode(5, "关胜");
        root.left = node2;
        root.right = node3;
        node3.right = node4;
        node3.left = node5;
        binaryTree.root = root;
        // System.out.println("前序遍历====>");
        // binaryTree.preOrder();
        // System.out.println("中序遍历====>");
        // binaryTree.infixOrder();
        // System.out.println("后续遍历====>");
        // binaryTree.postOrder();
        System.out.println("前序查找====>");
        HeroNode heroNode = binaryTree.preOrderSearch(5);
        System.out.println(heroNode);
        System.out.println("中序查找====>");
        heroNode = binaryTree.infixOrderSearch(5);
        System.out.println(heroNode);
        System.out.println("后续查找====>");
        heroNode = binaryTree.postOrderSearch(5);
        System.out.println(heroNode);
    }

    static class BinaryTree {
        public HeroNode root;

        //前序遍历
        public void preOrder() {
            if (this.root != null) {
                this.root.preOrder();
            } else {
                System.out.println("二叉树为空，无法遍历");
            }
        }
        public HeroNode preOrderSearch(int no) {
            if (this.root != null) {
                return this.root.preOrderSearch(no);
            } else {
                System.out.println("二叉树为空，无法遍历");
                return null;
            }
        }
        //中序遍历
        public void infixOrder() {
            if (this.root != null) {
                this.root.infixOrder();
            } else {
                System.out.println("二叉树为空，无法遍历");
            }
        }
        public HeroNode infixOrderSearch(int no) {
            if (this.root != null) {
                return this.root.infixOrderSearch(no);
            } else {
                System.out.println("二叉树为空，无法遍历");
                return null;
            }
        }
        public void postOrder() {
            if (this.root != null) {
                this.root.postOrder();
            } else {
                System.out.println("二叉树为空，无法遍历");
            }
        }
        public HeroNode postOrderSearch(int no) {
            if (this.root != null) {
                return this.root.postOrderSearch(no);
            } else {
                System.out.println("二叉树为空，无法遍历");
                return null;
            }
        }
    }

    static class HeroNode {
        public int no;
        public String name;
        public HeroNode left;
        public HeroNode right;

        public HeroNode(int no, String name) {
            this.no = no;
            this.name = name;
        }

        //编写前序遍历的方法
        public void preOrder() {
            //先输出父节点
            System.out.println(this);
            //递归左子树
            if (this.left != null) {
                this.left.preOrder();
            }
            //递归向右字数遍历
            if (this.right != null) {
                this.right.preOrder();
            }
        }

        //前序遍历查找
        public HeroNode preOrderSearch(int no) {
            System.out.println("preOrderSearch前序查找");
            if (this.no == no) {
                return this;
            }
            // 判断当前结点的左子节点是否为空，如果不为空，
            // 则递归前序查找
            HeroNode result = null;
            if (this.left != null) {
                result = this.left.preOrderSearch(no);
            }
            if (result != null) {//说明找到了
                return result;
            }
            //  否则续判断，当前的结点的右子节点是否为空，如果不空．则继续向右递归前序查找。
            if (this.right != null) {
                result = this.right.preOrderSearch(no);
            }
            return result;
        }

        //编写中序遍历的方法
        public void infixOrder() {
            //先递归左子树
            if (this.left != null) {
                this.left.infixOrder();
            }
            //输出父节点
            System.out.println(this);
            //递归向右子树遍历
            if (this.right != null) {
                this.right.infixOrder();
            }
        }

        //编写中序遍历的方法
        public HeroNode infixOrderSearch(int no) {
            HeroNode result = null;
            //先递归左子树
            if (this.left != null) {
                result = this.left.infixOrderSearch(no);
            }
            if (result != null) {
                return result;
            }
            // 因为有几次是空比较，所以需要加在比较的地方
            System.out.println("infixOrderSearch中序查找");
            if (this.no == no) {
                return this;
            }
            //递归向右子树遍历
            if (this.right != null) {
                result = this.right.infixOrderSearch(no);
            }
            return result;
        }
        //编写后序遍历的方法
        public void postOrder() {
            //先递归左子树
            if (this.left != null) {
                this.left.postOrder();
            }
            //递归向右子树遍历
            if (this.right != null) {
                this.right.postOrder();
            }
            //输出父节点
            System.out.println(this);
        }
        //编写后序遍历查找
        public HeroNode postOrderSearch(int no) {
            HeroNode result = null;
            //先递归左子树
            if (this.left != null) {
                result = this.left.postOrderSearch(no);
            }
            if (result != null) {
                return result;
            }
            //递归向右子树遍历
            if (this.right != null) {
                result = this.right.postOrderSearch(no);
            }
            if (result != null) {
                return result;
            }
            //如果左右子树都没找到，就比较当前节点是不是
            System.out.println("postOrderSearch后续查找");
            if (this.no == no) {
                return this;
            }
            return result;
        }

        @Override
        public String toString() {
            return "HeroNode{" +
                    "no=" + no +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}

