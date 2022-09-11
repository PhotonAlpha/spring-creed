package com.ethan.collection

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/31/2022 10:45 AM
 */
object ImmutableSetTest {
  def main(args: Array[String]): Unit = {
    val set = Set(1, 2, 3, 2)
    println(set)

    // 增加元素

    val newSet1 = set + 4
    println(newSet1)
    //合并
    val newSet2 = newSet1 ++ set
    println(newSet2)
    //删除元素
    val newSet3 = newSet2 - 1
    println(newSet3)

  }
}
