package com.ethan.tree;

/**
 * @className: AVLTreeDemo
 * @author: Ethan
 * @date: 30/7/2021
 **/
public class AVLTreeDemo {
    public static void main(String[] args) {
        // int[] arr = {4, 3, 6, 5, 7, 8};
        // int[] arr = {10, 12, 8, 9, 7, 6};
        int[] arr = {10, 11, 7, 6, 8, 9}; //无法旋转的情况
        //创建一个AVL 树
        AVLTree avlTree = new AVLTree();
        //添加节点
        for (int i = 0; i < arr.length; i++) {
            avlTree.add(new Node(arr[i]));
        }
        //中序遍历
        System.out.println("中序遍历");
        avlTree.infixOrder();
        System.out.println("在没有平衡处理之前~");
        System.out.println("树的高度：" + avlTree.root.height());
        System.out.println("左子树的高度：" + avlTree.root.leftHeight());
        System.out.println("右子树的高度：" + avlTree.root.rightHeight());
        System.out.println("根结点：" + avlTree.root);
        System.out.println("左子结点：" + avlTree.root.left);
        System.out.println("右子点：" + avlTree.root.right);

        //开始进行左旋转
    }

    static class AVLTree {
        public Node root;

        public void add(Node node) {
            if (root == null) {
                root = node;
            } else {
                root.add(node);
            }
        }

        //中序遍历
        public void infixOrder() {
            if (this.root != null) {
                this.root.infixOrder();
            } else {
                System.out.println("二叉排序树为空");
            }
        }

        //查找要删除的结点
        public Node search(int value) {
            if (root == null) {
                return null;
            } else {
                return this.root.search(value);
            }
        }

        //查找父结点
        public Node searchParent(int value) {
            if (root == null) {
                return null;
            } else {
                return this.root.searchParent(value);
            }
        }

        /**
         * @param node 传入的节点
         * @return 返回以node为跟结点的二叉排序树的最小结点的值 & 删除最小结点
         */
        public int delRightTreeMin(Node node) {
            Node target = node;
            //循环查找左结点，找到最小的值
            while (target.left != null) {
                target = target.left;
            }
            // target指向了最小结点
            //删除最小结点
            delNode(target.value);
            return target.value;
        }

        // 删除结点
        public void delNode(int value) {
            if (root == null) {
                return;
            } else {
                Node tarNode = search(value);
                //如果没找到，就返回
                if (tarNode == null) {
                    return;
                }
                //如果当前结点只有一个结点
                if (root.left == null && root.right == null) {
                    root = null;
                    return;
                }
                //去找tarNode的父结点
                Node parentNode = searchParent(value);
                // 1. 如果删除的结点是叶子结点
                if (tarNode.left == null && tarNode.right == null) {
                    //判断tarNode是父结点的左子结点还是右子结点
                    if (parentNode.left != null && parentNode.left.value == value) {
                        parentNode.left = null;
                    } else if (parentNode.right != null && parentNode.right.value == value) {
                        parentNode.right = null;
                    }
                } else if (tarNode.left != null && tarNode.right != null) {
                    // 3. 删除有两棵子树的节点，比如 3, 10, 7
                    int minVal = delRightTreeMin(tarNode.right);
                    tarNode.value = minVal;
                } else {
                    // 2. 删除只有一颗子树的结点
                    if (tarNode.left != null) {
                        if (parentNode != null) {
                            //要删除的是左子结点
                            if (parentNode.left != null && parentNode.left.value == value) {
                                parentNode.left = tarNode.left;
                            } else if (parentNode.right != null && parentNode.right.value == value) {
                                parentNode.right = tarNode.left;
                            }
                        } else {
                            root = tarNode.left;
                        }
                    } else {
                        if (parentNode != null) {
                            //要删除的是右子结点
                            if (parentNode.left != null && parentNode.left.value == value) {
                                parentNode.left = tarNode.right;
                            } else if (parentNode.right != null && parentNode.right.value == value) {
                                parentNode.right = tarNode.right;
                            }
                        } else {
                            root = parentNode.right;
                        }
                    }
                }

            }
        }
    }

    //创建node节点
    static class Node {
        public int value;
        public Node left;
        public Node right;

        public Node(int value) {
            this.value = value;
        }
        //返回左子树的高度
        public int leftHeight() {
            if (left == null) {
                return 0;
            }
            return left.height();
        }
        //返回右子树的高度
        public int rightHeight() {
            if (right == null) {
                return 0;
            }
            return right.height();
        }

        //返回该结点为根结点的高度,以该结点为根结点的树的高度
        public int height() {
            return Math.max(left == null ? 0 : left.height(), right == null ? 0 : right.height()) + 1;
        }

        //左旋方法
        private void leftRotate() {
            //创建新的结点，以当前根结点的值
            Node newNode = new Node(value);
            //1.把新的结点的左子树设置成当前节点的左子树
            newNode.left = left;
            //2.把新的结点的右子树设置成当前结点的右子树的左子树
            newNode.right = right.left;
            //3.把当前结点的值替换成右子结点的值
            value = right.value;
            //4.把当前结点的右子树的右子树
            right = right.right;
            //5.把当前结点的左子树(左子结点)设置成新的结点
            left = newNode;
        }
        //左旋方法
        private void rightRotate() {
            //创建新的结点，以当前根结点的值
            Node newNode = new Node(value);
            //1.把新的结点的右子树设置成当前节点的右子树
            newNode.right = right;
            //2.把新的结点的左子树设置成当前结点的左子树的右子树
            newNode.left = left.right;
            //3.把当前结点的值替换成左子结点的值
            value = left.value;
            //4.把当前结点的左子树的左子树
            left = left.left;
            //5.把当前结点的右子树(右子结点)设置成新的结点
            right = newNode;
        }

        // 添加节点
        //递归的形式添加节点，需要满足二叉排序树
        public void add(Node node) {
            if (node == null) {
                return;
            }
            //判断传入结点的值和当前子树的根节点的值的关系
            if (node.value < this.value) {
                //如果当前结点左子结点为空
                if (this.left == null) {
                    this.left = node;
                } else {
                    //递归向左子树添加
                    this.left.add(node);
                }
            } else {
                //添加的结点的值大于当前结点的值
                if (this.right == null) {
                    this.right = node;
                } else {
                    //递归向右子树添加
                    this.right.add(node);
                }
            }

            //当添加完一个结点后，如果（右子树的高度-左子树的高度) > 1,左旋转
            if (rightHeight() - leftHeight() > 1) {
                //如果它的右子树的左子树高度大于它的右子树的高度
                if (right != null && right.rightHeight() < right.leftHeight()) {
                    //先对当前结点的右子结点进行右旋转
                    right.rightRotate();
                }
                leftRotate();//左旋转

                return;//直接返回
            }

            //当添加完一个结点后，如果（左子树的高度 - 右子树的高度) > 1,右旋转
            if (leftHeight() - rightHeight() > 1) {
                //如果它的左子树的右子树高度大于它的左子树的高度
                if (left != null && left.rightHeight() > left.leftHeight()) {
                    //先对当前结点的左子结点进行左旋转
                    left.leftRotate();
                }
                rightRotate();//右旋转
                return;//直接返回
            }
        }

        /**
         *
         * @param value
         * @return 如果找到返回结点，否则返回null
         */
        //删除结点
        public Node search(int value) {
            if (value == this.value) {
                return this;
            } else if (value < this.value) {
                //如果小于当前节点，向左子树递归查找
                if (this.left == null) {
                    return null;
                } else {
                    return this.left.search(value);
                }
            } else {
                //查找的值大于等于，向右查找
                if (this.right == null) {
                    return null;
                } else {
                    return this.right.search(value);
                }
            }


        }

        //查找要删除结点的父结点
        /**
         *
         * @param value
         * @return 返回要删除结点的父结点，如果没有就返回null
         */
        public Node searchParent(int value) {
            //如果当前结点就是要删除的结点的父结点，就返回
            if ((this.left != null && this.left.value == value)
                    || (this.right != null && this.right.value == value)) {
                return this;
            } else {
                //如果查找的值小于当前结点的值，并且当前结点的左子结点不为空
                if (value < this.value && this.left != null) {
                    return this.left.searchParent(value);
                } else if (value >= this.value && this.right != null) {
                    return this.right.searchParent(value);
                } else {
                    return null;
                }
            }
        }

        //中序遍历
        public void infixOrder() {
            if (this.left != null) {
                this.left.infixOrder();
            }
            System.out.println(this);
            if (this.right != null) {
                this.right.infixOrder();
            }
        }

        @Override
        public String toString() {
            return "Node{" +
                    "value=" + value +
                    '}';
        }
    }
}
