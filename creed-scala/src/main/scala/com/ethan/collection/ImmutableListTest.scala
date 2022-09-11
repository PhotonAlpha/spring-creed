package com.ethan.collection

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/31/2022 10:45 AM
 */
object ImmutableListTest {
  def main(args: Array[String]): Unit = {
    val list = List[Int](1, 2, 3)
    println(list)

    val newArr1 = list :+ 11
    println(newArr1)
    val newArr2 = 21 +: list
    println(newArr2)


    val newList2 = list.::(10)
    println("newList2" + newList2)

    val newList3 = 10 :: 28 :: 59 :: 9 :: Nil
    println("newList3" + newList3)

    val newList4 = newList2 ::: newList3
    val newList5 = newList2 ++ newList3
    println("newList4" + newList4)
    println("newList5" + newList5)

  }
}
