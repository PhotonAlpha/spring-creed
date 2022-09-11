package com.learnscala.collection

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/2/2022 5:57 PM
 */
object TupleTest {
  def main(args: Array[String]): Unit = {
    //元组, 最大元素数量是22
    val tuple = ("a", 1, '2', true)
    println(tuple)
    println(tuple._1)
    println(tuple._2)
    println(tuple._3)
    println(tuple._4)
    println("------")
    for (elem <- tuple.productIterator) {
      println(s"$elem")
    }
  }

}
