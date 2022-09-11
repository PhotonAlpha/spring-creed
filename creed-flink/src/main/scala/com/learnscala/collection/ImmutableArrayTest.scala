package com.learnscala.collection

import scala.+:

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/31/2022 10:45 AM
 */
object ImmutableArrayTest {
  def main(args: Array[String]): Unit = {
    val arr = Array[Int](1, 2, 3)
    val arr2 = new Array[Int](5)
    arr2(0) = 100
    arr2(1) = 111
    arr2(2) = 222
    arr2(3) = 333
    arr2(4) = 444
    // arr2.apply(6)


    println(arr)
    println(arr2)
    for (i <- arr2.indices) {
      println(s"elem ${arr2(i)}")
    }
    println("============")
    for (elem <- arr2) {
      println(s"elem $elem")
    }

    println("============")
    arr2.foreach(println)
    println(arr2.mkString(","))

    //4.添加元素
    //向后添加
    val newArr = arr2.:+(73)
    val newArr_1 = arr2 :+ 99
    println("newArr:"+newArr.mkString(","))
    println("newArr_1:"+newArr_1.mkString(","))

    // . () 只有一个参数，可以省略
    //向前添加
    val newArr2 = arr2.+:(73)
    val newArr2_1 = 19 +: 66 +: newArr2
    println("newArr2:"+newArr2.mkString(","))
    println("newArr2_1:"+newArr2_1.mkString(","))

  }
}
