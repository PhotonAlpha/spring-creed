package com.ethan.collection

import scala.collection.mutable

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/31/2022 10:45 AM
 */
object MutableSetTest {
  def main(args: Array[String]): Unit = {
    val set = mutable.Set(1, 2, 3, 2)
    println(set)

    // 增加元素
    set += 4
    println(set)
    //合并
    val set2 = mutable.Set(4, 5, 3, 2)
    println(set2)
    //删除元素
    val newSet3 = set2 - 1
    println(newSet3)

    val res = set2 -= 5
    println("res" + res)

    //合并
    val set3 = set ++ set2
    println("set3" + set3)


    println("-----")
    val res2 = set ++= set2
    println("res2" + res2)
    println("set" + set)
    println("set2" + set2)
    println(res2 == set)
    println(res2 == set2)

  }
}
