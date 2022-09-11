package com.learnscala.collection

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/5/2022 2:56 PM
 */
object SimpleFunction {
  def main(args: Array[String]): Unit = {
    val list1 = List(5, 2, 3, 5, 2, 8)
    val list2 = List(("b", 5), ("a", 2), ("d", 3), ("c", 5), ("e", 2), ("f", 8))
    // 求和
    println("---求和---")
    println(list1.sum)
    println(list1.product)
    println(list1.max)
    println(list1.min)
    println(list1.sorted)
    println(list1.sorted(Ordering[Int].reverse))
    println(list2.sortBy(_._2)(Ordering[Int].reverse))


    // println(list2.sortWith((a, b) => {
    //   a._2 > b._2
    // } ))
    println(list2.sortWith(_._2 > _._2))
    // println(list2.sortWith(_._2 > _._2) sortWith(_._1 < _._1))

    // println(list2.maxBy((tuple: (String, Int)) => tuple._2))
    println(list2.maxBy(_._2))


    println("---求乘积---")
    println(list2.map(_._2))
  }
}
