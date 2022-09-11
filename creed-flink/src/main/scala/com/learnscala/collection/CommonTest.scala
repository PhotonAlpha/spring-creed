package com.learnscala.collection

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/5/2022 11:15 AM
 */
object CommonTest {
  def main(args: Array[String]): Unit = {
    val list1 = List(1, 2, 3, 5, 7, 8)
    val set = Set(1, 33, 44, 2)
    val list2 = List(9, 10, 11)
    val set2 = Set(2, 3, 44, 2)
    //获取集合长度
    println(list1.length)
    //获取集合大小
    println(list1.size)

    // 获取集合的头
    val head = list1.head
    // 除去头就是尾
    val t = list1.tail
    println(head)
    println(t)
    println(list1.last)
    // 集合的初始元素，不是最后一个的其他元素
    println(list1.init)
    println(list1.reverse)
    // 获取前三个元素
    println(list1.take(3))
    // 从右边开始的N个元素
    println(list1.takeRight(4))

    // 去掉前三个元素
    println(list1.drop(3))
    // 去掉从右边开始的N个元素
    println(list1.dropRight(4))

    println(set.size)
    println("---合并集合--")
    println(list1.union(list2))
    println(set.union(set2))
    println(set ++ set2)
    println("---交集--")
    println(list1.intersect(list2))
    println(set.intersect(set2))
    println("---差集--")
    println(list1.diff(list2))
    println(set.diff(set2))
    println("---拉链--")
    println(s"${list1.zip(list2)} -- ${list2.zip(list1)}")
    println(s"${set.zip(set2)} -- ${set2.zip(set)}")
    println("---滑窗--")
    println(s"${for (elem <- list1.sliding(3, 2)) {println(elem)}}")
    println(s"${for (elem <- set.sliding(3, 2)) {println(elem)}}")
    // 循环遍历

    // 迭代器

    // 生成字符串

    // 是否包含
  }
}
