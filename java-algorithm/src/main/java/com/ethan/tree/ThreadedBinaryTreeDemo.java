package com.ethan.tree;

/**
 * @className: Threaded * @author: Ethan
 * @date: 15/7/2021
 **/
public class ThreadedBinaryTreeDemo{
    public static void main(String[] args) {
        HeroNode root = new HeroNode(1, "宋江");
        HeroNode node1 = new HeroNode(3, "吴用");
        HeroNode node2 = new HeroNode(8, "卢俊义");
        HeroNode node3 = new HeroNode(10, "林冲");
        HeroNode node4 = new HeroNode(6, "关胜");
        HeroNode node5 = new HeroNode(14, "关胜");
        root.left = node1;
        root.right = node4;
        node1.left = node2;
        node1.right = node3;
        node4.left = node5;
        /**
         * {8, 3, 10, 1, 14, 6}
         */
        ThreadedBinaryTree threadedBinaryTree = new ThreadedBinaryTree();
        threadedBinaryTree.root = root;
        //测试线索化
        threadedBinaryTree.threadedNodes(root);
        //测试10号节点
        System.out.print(node3 + " =>");
        System.out.print("10号节点的前驱："+node3.left+" =>");
        System.out.println("10号节点的后继：" + node3.right);
        //当线索化二叉树后，不能再使用原来的遍历方式
        /**
         * 说明：对前面的中序线索化的二叉树，进行遍历
         * 分析：因为线索化后，各个结点指向有变化，因此**原来的遍历方式不能使用,**
         *  这时需要使用新的方式遍历线索化二叉树，各个节点可以通过线性方式遍历，
         * 因此无需使用递旧方式，这样也提高了遍历的效率。**遍历的次序应当和中序遍历保持一致**
         */
        // threadedBinaryTree.infixOrder();
        /*使用线索化的方式遍历线索化二叉树*/
        threadedBinaryTree.threadedList();

    }
    static class ThreadedBinaryTree {
        public HeroNode root;
        //为了实现线索化，需要创建指向当前节点的前驱节点的指针
        // 在递归进行线索化时，pre总是保留前一个节点
        public HeroNode pre = null;

        // 对二叉树进行中序线索化的方法
        /**
         * @param node 就是当前需要线索化的节点
         */
        public void threadedNodes(HeroNode node) {
            //如果为空，不能线索化
            if (node == null) {
                return;
            }
            //因为是中序遍历，
            //1. 先线索化左子树
            threadedNodes(node.left);
            //!!!2. 线索化当前节点
            //2.1先处理当前节点的前驱节点
            //  以8节点来理解，8.left = null, 8.leftType=1
            if (node.left == null) {
                //让当前节点的左指针指向前驱节点
                node.left = pre;
                //修改当前节点的左指针的类型，指向前驱节点
                node.leftType = 1;
            }
            //2.2处理后继节点
            if (pre != null && pre.right == null) {
                //让前驱节点的右指针指向当前节点
                pre.right = node;
                pre.rightType = 1;
            }
            //!!!2.3每处理一个节点后，让当前节点是下一个节点的前驱节点
            pre = node;

            //3. 线索化右子树
            threadedNodes(node.right);
        }
        //中序遍历线索化二叉树
        public void threadedList() {
            if (this.root != null) {
                HeroNode node = this.root;
                while (node != null) {
                    //循环的找到leftType=1的节点，第一个找到的就是8节点
                    // 后面随着遍历而变化，因为leftType=1时，说明该节点是按照线索化处理后的有效节点
                    while (node.leftType == 0) {
                        node = node.left;
                    }
                    //打印当前节点
                    System.out.println(node);
                    //如果当前节点的右指针指向的是后继节点，就一直输出
                    while (node.rightType == 1) {
                        //获取到当前节点的后继节点
                        node = node.right;
                        System.out.println(node);
                    }
                    //替换这个遍历的节点
                    node = node.right;
                }
            } else {
                System.out.println("二叉树为空，无法遍历");
            }
        }


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
        //中序遍历线索化二叉树
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
        public void delNode(int no) {
            if (this.root != null) {
                //这里需要判断root是否是要删除的节点
                if (root.no == no) {
                    this.root = null;
                } else {
                    this.root.delNode(no);
                }
            } else {
                System.out.println("二叉树为空，无法删除");
            }
        }

    }

    //创建HeroNode
    static class HeroNode {
        public int no;
        public String name;
        public HeroNode left;
        public HeroNode right;
        //1. 如果leftType==0表示指向左子树，如果是1则指向前驱节点
        //2. 如果rightType==0表示指向右子树，如果是1则指向后继节点
        public int leftType;
        public int rightType;

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

        //递归删除节点
        /**
         * 1. 由于现在的二叉树是单向的，所以我们是判断当前节点的子节点是否需要删除，而不能去判断当前节点是否需要删除
         * 2. 如果当前节点的左子节点不为空，并且左子节点就是要删除的节点，就将this.left=null并且就返回（结束递归删除）
         * 3. 如果当前节点的右子节点不为空，并且右子节点就是要删除的节点，就将this.right=null并且返回
         * 4. 如果2.3.都没有删除节点，向左子树进行递归删除
         * 5. 如果  4. 没有删除节点，向右子树进行递归删除
         * 6. 如果只有一个root节点，则将二叉树置空
         * @param no
         */
        public void delNode(int no) {
            if (this.left != null && this.left.no == no) {
                this.left = null;
                return;
            }
            if (this.right != null && this.right.no == no) {
                this.right = null;
                return;
            }
            //向左子树递归
            if (this.left != null) {
                this.left.delNode(no);
            }
            if (this.right != null) {
                this.right.delNode(no);
            }

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
