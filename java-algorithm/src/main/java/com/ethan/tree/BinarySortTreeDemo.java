package com.ethan.tree;

/**
 * @className: BinarySortTreeDemo
 * @author: Ethan
 * @date: 22/7/2021
 *               7
 *             /    \
 *            3     10
 *           / \    / \
 *          1   5  9   12 中序=> 1 2 3 5 7 9 10 12
 *           \
 *            2
 *  删除结点
 *  1） 删除 2,5,9,12
 *      1.需要去找要删除结点的tarNode
 *      2.找到tarNode的parentNode
 *      3.确定tarNode是parentNode的左子结点还是右子结点
 *  2）删除只有一颗子树的节点，比如 1
 *      1.需要去找要删除结点的tarNode
 *      2.找到tarNode的parentNode
 *      3.确定tarNode的结点点是左子结点还是右子结点
 *      4.tarNode是parentNode的左子结点还是右子结点
 *      5. 如果tarNode是左子结点
 *          5.1 parent.left = tarNode.left
 *          5.2 parent.left = tarNode.right
 *      6. 如果tarNode是右子结点
 *          5.1 parent.right = tarNode.left
 *          5.2 parent.right = tarNode.right
 *  3）删除有两棵子树的节点，比如 3, 10, 7
 *       1.需要去找要删除结点的tarNode
 *       2.找到tarNode的parentNode
 *       3.确定tarNode的右子树找到最小的结点 | 从左子树找最大的
 *       4.用一个临时变量保存最小值 tmp
 *       5.删除该最小结点
 *       6.tarNode.value=tmp
 **/
public class BinarySortTreeDemo {
    public static void main(String[] args) {
        int[] arr = {7, 3, 10, 12, 5, 1, 9, 2};
        BinarySortTree sortTree = new BinarySortTree();
        for (int i : arr) {
            sortTree.add(new Node(i));
        }
        sortTree.infixOrder();
        sortTree.delNode(12);
        sortTree.delNode(7);
        sortTree.delNode(3);
        sortTree.delNode(5);
        sortTree.delNode(9);
        sortTree.delNode(2);
        sortTree.delNode(10);
        // sortTree.delNode(1);
        System.out.println("删除结点后");
        sortTree.infixOrder();
    }

    // 创建二叉排序树
    static class BinarySortTree {
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
