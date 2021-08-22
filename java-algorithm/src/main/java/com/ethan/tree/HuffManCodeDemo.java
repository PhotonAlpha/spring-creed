package com.ethan.tree;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @className: HuffManCodeDemo
 * @author: Ethan
 * @date: 19/7/2021
 *
 * 根据赫夫曼编码压缩的原理，需要创建 "i like like like java do you like a java"对应的赫夫曼树
 * 思路：
 * （1）Node{ data（存放数据），weight（权值），left和right }
 * （2）得到"i like like like java do you like a java" 对应的 byte[]数组
 * （3）编写一个方法，将准备构建赫夫曼树的Node节点放到List，形式[Node[date=7,weight=51], Node[]date=32,weight=91......1] 体现d:1 y:1 u:1 j:2 v:2 o:2 l:4 k:4 e:4 i:5 a:5 :9
 * （4）可以过List创建对应的赫夫曼树
 **/
public class HuffManCodeDemo {
    public static void main(String[] args) {
        String str = "i like like like java do you like a java";
        /*byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        // System.out.println(Arrays.toString(strBytes));
        List<Node> nodes = getNodes(strBytes);
        Node huffManTree = createHuffManTree(nodes);
        // System.out.println(nodes);
        //查看值
        preOrder(huffManTree);
        getHuffManCode(huffManTree, "", new StringBuilder());
        System.out.println(huffmanCodes);

        byte[] zip = zip(strBytes, huffmanCodes);*/
        byte[] zip = huffmanZip(str.getBytes(StandardCharsets.UTF_8));
        System.out.println("编码后长度:"+zip.length);
        System.out.println("编码后长度:"+Arrays.toString(zip));
        byte[] huffmanUnzip = huffmanUnzip(huffmanCodes, zip);
        System.out.println("解码后的值=" + new String(huffmanUnzip, StandardCharsets.UTF_8));
        // 1010100010111111110010001011111111001000101111111100100101001101110001110000011011101000111100101000101111111100110001001010011011100
        // 1010100010111111110010001011111111001000101111111100100101001101110001110000011011101000111100101000101111111100110001001010011011100
    }

    /**
     * 1. 将huffmanCodeBytes [-88, -65, -56, -65, -56, -65, -55, 77, -57, 6, -24, -14, -117, -4, -60, -90, 28]
     * 重写先转成 赫夫曼对应的二进制字符串”1010.。。“
     * 2. 赫夫曼编码对用的二进制的字符串”1010...“ => 对照赫夫曼编码表 => "i like..."
     *
     * @param huffmanCodes
     * @param huffmanBytes 赫夫曼编码的字节数组
     * @return 返回原来的字符串对应的数组
     */
    public static byte[] huffmanUnzip(Map<Byte, String> huffmanCodes, byte[] huffmanBytes) {
        // 1. 先得到huffmanBytes对应的二进制字符串
        StringBuilder sb = new StringBuilder();
        // 2. 将byte数组转成二进制的字符串
        for (int i = 0; i < huffmanBytes.length; i++) {
            boolean flag = i == huffmanBytes.length - 1;
            sb.append(byteToBitString(!flag, huffmanBytes[i]));
        }
        System.out.println("赫夫曼字节数组对应的二进制字符串=" + sb);
        //把字符串按照指定的赫夫曼编码进行解码
        //把赫夫曼编码进行调换，进行反向查询
        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<Byte, String> entry : huffmanCodes.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        //创建集合存放byte
        List<Byte> list = new ArrayList<>();
        for (int i = 0; i < sb.length();) {
            int count = 1;
            boolean flag = true;
            Byte b = null;
            while (flag) {
                String key = sb.substring(i, i + count);//i不动，count移动，知道拿到字符
                b = map.get(key);
                if (b != null) {
                    flag = false;
                } else {
                    count++;
                }
            }
            list.add(b);
            i += count;
        }
        //当for循环结束后，list中就存放了所有的字符
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }

    /**
     * 将一个byte转成一个二进制的字符串
     * @pram flag 标志是否需要补高位，如果是true，表示需要补高位，如果是false表示不补
     * @param b 对用的二进制的字符串（补码返回）
     * @return
     */
    private static String byteToBitString(boolean flag, byte b) {
        int tmp = b; //将b转成int
        //如果是正数我们还存在补高位
        if (flag) {
            tmp |= 256; // 256 => 1 0000 0000, 按位与
        }

        String str = Integer.toBinaryString(tmp); //返回的tmp的二进制的补码
        System.out.println("转换字符串：" + str);
        if (flag) {
            return str.substring(str.length() - 8);
        } else {
            return str;
        }
    }

    //使用一个方法，封装起来，便于调用
    public static byte[] huffmanZip(byte[] bytes) {
        List<Node> nodes = getNodes(bytes);
        Node huffManTree = createHuffManTree(nodes);
        getHuffManCode(huffManTree, "", new StringBuilder());
        return zip(bytes, huffmanCodes);
    }

    /**
    //编写一个方法，将一个字符串对应的byte[]数组，通过生成的赫夫曼编码表，返回一个赫夫曼编码压缩后的byte[]
     * @param bytes 原始的字符串对应的byte[]
     * @param huffmanCodes 赫夫曼编码map
     * @return 返回赫夫曼编码处理后的byte[],
     * 返回字符串”101010001011111....“
     * => 对应的byte[] huffmanCodeBytes[0] = 10101000(补码) => byte[10101000-1 => 10100111(反码) => 11011000 => -88]
     */
    private static byte[] zip(byte[] bytes, Map<Byte, String> huffmanCodes) {
        System.out.println(Integer.parseInt("10101000", 2));
        //1.利用huffmanCode将bytes转成赫夫曼编码对应的字符串
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(huffmanCodes.get(aByte));
        }
        System.out.println("生成对应的赫夫曼编码是:" + sb);

        //将字符串转成byte[]
        // 统计返回byte[] huffmanCodeBytes长度
        // int len;
        // if (sb.length() % 8 == 0) {
        //     len = sb.length() / 8;
        // } else {
        //     len = sb.length() / 8 + 1;
        // }
        //一句话
        int len = (sb.length() + 7) / 8;
        //创建存储压缩后的byte[]
        byte[] huffmanCodeBytes = new byte[len];
        int index = 0;
        for (int i = 0; i < sb.length(); i += 8) {
            //每8位对应一个byte
            String substr;
            if (i + 8 > sb.length()) {
                //不够8位
                substr = sb.substring(i);
            } else {
                substr = sb.substring(i, i + 8);
            }
            // 将strByte转成一个byte,放入到huffmanCodeBytes
            huffmanCodeBytes[index] = (byte) Integer.parseInt(substr, 2);
            index++;
        }
        //创建huffmanCodeBytes
        return huffmanCodeBytes;

    }

    private static List<Node> getNodes(byte[] bytes) {
        //创建ArrayList
        List<Node> nodes = new ArrayList<>();
        //存储每一个byte出现的次数,统计每个byte出现的次数 -> map
        Map<Byte, Integer> map = new HashMap<>();
        for (byte aByte : bytes) {
            map.merge(aByte, 1, Integer::sum);
        }
        //把每个键值对转成一个node对象，，并加入到node中
        for (Map.Entry<Byte, Integer> entry : map.entrySet()) {
            nodes.add(new Node(entry.getKey(), entry.getValue()));
        }
        return nodes;
    }

    static Map<Byte, String> huffmanCodes = new HashMap<>();
    /**
    //生成赫夫曼树对应的赫夫曼编码
    //1. 将赫夫曼编码表存放在map中， Map<Byte,String>
    //2. 在生成赫夫曼编码表时，需要去拼接路径，存储某个叶子节点的路径
     * @param node
     * @param code 左子节点是0，右子节点时1
     * @param strBuilder
     */
    public static void getHuffManCode(Node node, String code, StringBuilder strBuilder) {
        StringBuilder sb = new StringBuilder(strBuilder);
        sb.append(code);
        if (node != null) {
            //判断当前node是叶子节点还是非叶子节点
            if (node.data == null) {
                //非叶子节点，递归处理
                //向左
                getHuffManCode(node.left, "0", sb);
                //向右递归
                getHuffManCode(node.right, "1", sb);
            } else {
                //说明是叶子节点，就表示找到了某个叶子结点的
                huffmanCodes.put(node.data, sb.toString());
            }
        }
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
     * @param nodes
     * @return 返回创建好的赫夫曼树的节点
     */
    public static Node createHuffManTree(List<Node> nodes) {
        // 1. 为了操作方便，遍历 arr数组
        // 2.将arr的每个元素构建成一个node
        // 3.将node放入ArrayList中
        //循环处理***********************
        while (nodes.size() > 1) {

            // 排序从小打到
            Collections.sort(nodes);

            // System.out.println("nodes" + nodes);
            //1.取出权值最小的二叉树
            Node leftNode = nodes.get(0);
            //2.取出权值第二小的二叉树
            Node rightNode = nodes.get(1);
            //3. 构建一颗新的二叉树
            Node parent = new Node(null, leftNode.weight + rightNode.weight);
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
        Byte data;//存放数据本身，比如 ’a‘ => 97
        int weight;//节点权值
        Node left;//指向左子节点
        Node right;//指向右子节点

        public Node(Byte data, int weight) {
            this.data = data;
            this.weight = weight;
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
                    "data=" + data +
                    ", weight=" + weight +
                    '}';
        }

        @Override
        public int compareTo(Node o) {
            //表示从小到大排序
            return this.weight - o.weight;
        }
    }
}
