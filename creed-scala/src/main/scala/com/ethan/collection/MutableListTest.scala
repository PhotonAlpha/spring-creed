package com.ethan.collection

import scala.collection.mutable.ListBuffer

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/31/2022 10:45 AM
 */
object MutableListTest {
  def main(args: Array[String]): Unit = {
    val list = ListBuffer[Int](1, 2, 3)
    println(list)

    list.append(11)
    println(list)
    list.prepend(21)
    println(list)

    val newList3 = 10 :: 28 :: 59 :: 9 :: Nil
    println("newList3" + newList3)

    list.appendAll(newList3)
    val newList4 =  list ++ newList3
    println("list" + list)
    println("newList4" + newList4)


    list+=100
    println("list" + list)

    99 +=: list
    println("list" + list)

    list ++= newList3
    println("list" + list)
    println("newList3" + newList3)

    newList3 ++=: list
    println("list" + list)
    println("newList3" + newList3)


    list -= 59
    println("list" + list)
  }
}
