/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.hash;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 5/10/2023 10:53 AM
 * <p>
 * 如何验证hashMap中 value是在数组中还是链表中
 */
public class HashMapTest {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        HashMap<Student, String> map = new HashMap<>();

        // 添加元素
        for (int i = 0; i < 10000; i++) {
            map.put(new Student(i, i), "Value " + i );
        }

        Class<?> clazz = map.getClass();
        Field tableField = clazz.getDeclaredField("table");
        tableField.setAccessible(true);
        Object[] table = (Object[]) tableField.get(map);
        for (int i = 0; i < table.length; i++) {
            Object o = table[i];
            if (o != null) {
                Class<?> nodeClass = o.getClass();
                if (nodeClass.getName().endsWith("TreeNode")) {
                    System.out.println("Tree at index " + i);
                } else if (nodeClass.getName().endsWith("Node")) {
                    System.out.println("List at index " + i);
                }
            }
        }
    }

    static class Student {
        private int seq;
        private int val;

        public Student(int seq, int val) {
            this.seq = seq;
            this.val = val;
        }

        @Override
        public int hashCode() {
            return seq % 10;
        }
    }
}
