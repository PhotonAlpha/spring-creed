package com.ethan.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @className: GraphDemo
 * @author: Ethan
 * @date: 2/8/2021
 **/
public class GraphDemo {
    private List<String> vertexList;//存储顶点集合
    private int[][] edges;//存储图对应的领结矩阵
    private int numOfEdges;//表示边的数目
    //定义给数组boolean[], 记录某个结点是否被访问
    private boolean[] isVisited;
    // private int n = 5;
    private static int size = 8;

    public static void main(String[] args) {
        // String[] vertexValue = {"A", "B", "C", "D", "E"};
        String[] vertexValue = {"1", "2", "3", "4", "5", "6", "7", "8"};
        //创建图对象
        GraphDemo graphDemo = new GraphDemo(size);
        //循环添加顶点
        for (String s : vertexValue) {
            graphDemo.inertVertex(s);
        }
        //添加边
        // A-B A-C B-C B-D B-E
        // graphDemo.inertEdge(0, 1, 1);
        // graphDemo.inertEdge(0, 2, 1);
        // graphDemo.inertEdge(1, 2, 1);
        // graphDemo.inertEdge(1, 3, 1);
        // graphDemo.inertEdge(1, 4, 1);
        graphDemo.inertEdge(0, 1, 1);
        graphDemo.inertEdge(0, 2, 1);
        graphDemo.inertEdge(1, 3, 1);
        graphDemo.inertEdge(1, 4, 1);
        graphDemo.inertEdge(3, 7, 1);
        graphDemo.inertEdge(4, 7, 1);
        graphDemo.inertEdge(2, 5, 1);
        graphDemo.inertEdge(2, 6, 1);
        graphDemo.inertEdge(5, 6, 1);
        //显式领结矩阵
        graphDemo.showGraph();
        //测试dfs遍历
        System.out.println("开始遍历dfs");
        graphDemo.dfs();
        System.out.println();
        System.out.println("开始遍历bfs");
        graphDemo.bfs();
    }

    public GraphDemo(int n) {
        //初始化矩阵和vertexList
        edges = new int[n][n];
        vertexList = new ArrayList<>(n);
        this.numOfEdges = 0;
        isVisited = new boolean[5];
    }

    // 得到第一个领接结点的下标
    /**
     * @param index
     * @return 如果存在就返回对应的下标，否则返回-1
     */
    public int getFirstNeighbor(int index) {
        for (int j = 0; j < vertexList.size(); j++) {
            if (edges[index][j] > 0) {
                return j;
            }
        }
        return -1;
    }

    // 根据前一个领接结点的下标来获取下一个领接结点
    public int getNextNeighbor(int v1, int v2) {
        for (int j = v2 + 1; j < vertexList.size(); j++) {
            if (edges[v1][j] > 0) {
                return j;
            }
        }
        return -1;
    }

    //深度优先遍历算法
    public void dfs(boolean[] isVisited,int i) {
        //首先访问结点输出
        System.out.print(getValueByIndex(i) + "->");
        //将结点设置为已访问
        isVisited[i] = true;
        //查找结点i的第一个领接结点
        int w = getFirstNeighbor(i);
        while (w != -1) {//说明有
            if (!isVisited[w]) {
                //对W进行访问
                dfs(isVisited, w);
            }
            //如果W结点已经被访问过，查找领接结点的下一个结点
            w = getNextNeighbor(i, w);
        }
    }

    //对dfs进行重载，遍历所有结点进行DFS
    public void dfs() {
        isVisited = new boolean[size];
        //遍历所有的结点进行dfs[回溯]
        for (int i = 0; i < getNumOfVertex(); i++) {
            if (!isVisited[i]) {
                dfs(isVisited, i);
            }
        }
    }

    //对一个节点进行广度优先遍历方法
    private void bfs(boolean[] isVisited, int i) {
        int u;//表示队列的头结点对应下标
        int w;//领接结点w
        //队列，结点访问的顺序
        LinkedList<Integer> queue = new LinkedList<>();
        //访问结点
        System.out.print(getValueByIndex(i) + "->");
        //标记为已访问
        isVisited[i] = true;
        //将结点加入队列
        queue.addLast(i);
        while (!queue.isEmpty()) {
            //取出队列的头结点下标
            u = queue.removeFirst();
            w = getFirstNeighbor(u);
            while (w != -1) {//找到
                // 是否访问过
                if (!isVisited[w]) {
                    System.out.print(getValueByIndex(w) + "->");
                    //标记已经访问
                    isVisited[w] = true;
                    //入队
                    queue.addLast(w);
                }
                //以u为结点,找w后面的下一个领接点
                w = getNextNeighbor(u, w);//体现出广度优先
            }
        }
    }
    //遍历所有结点，都进行广度优先搜索
    public void bfs() {
        isVisited = new boolean[size];
        for (int i = 0; i < getNumOfVertex(); i++) {
            if (!isVisited[i]) {
                bfs(isVisited, i);
            }
        }
    }

    //图中常用的方法
    public int getNumOfVertex() {
        return vertexList.size();
    }
    //得到边的数目
    public int getNumOfEdges() {
        return numOfEdges;
    }

    //返回结点i(下标)对应的数据0->"A" 1->"B" 2->"C"
    public String getValueByIndex(int i) {
        return vertexList.get(i);
    }

    //返回v1和v2的权值
    public int getWeight(int v1, int v2) {
        return edges[v1][v2];
    }
    //显式图对应的矩阵
    public void showGraph() {
        for (int i = 0; i < edges.length; i++) {
            System.out.println(Arrays.toString(edges[i]));
        }
    }

    //插入结点
    public void inertVertex(String vertex) {
        vertexList.add(vertex);
    }

    //添加边
    /**
     * @param v1 表示点的下标，即第几个定点 "A"-"B" "A"->0 "B"->1
     * @param v2 表示第二个顶点对应的下标
     * @param weight
     */
    public void inertEdge(int v1, int v2, int weight) {
        edges[v1][v2] = weight;
        edges[v2][v1] = weight;
        numOfEdges++;
    }
}
