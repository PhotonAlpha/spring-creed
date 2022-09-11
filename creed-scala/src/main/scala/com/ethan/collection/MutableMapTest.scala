package com.ethan.collection

import scala.collection.mutable

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/31/2022 10:45 AM
 */
object MutableMapTest {
  def main(args: Array[String]): Unit = {
    val map1 = mutable.Map("a" -> 1, "b" -> 2)
    println(map1)
    println(map1.getClass)

    //添加元素
    map1.put("c", 3)
    map1 += "d" -> 4

    //遍历元素
    // map1.foreach((kv:( String, Int)) => println(kv))
    for (key <- map1) {
      println(s"key:${key._1} value:${key._2}")
    }
    /*println(s"val:${map1.get("c").get}")*/
    println(s"val:${map1.getOrElse("c", -1)}")
    // println(map1("c"))


    //删除元素
    println("------")
    map1 -= "d"
    map1.foreach((kv:( String, Int)) => print(kv))

    println("---合并集合---")
    //合并集合
    val map2 = mutable.Map("q" -> 10, "e" -> 11)
    val map3 = map1 ++= map2
    println(map1)
    println(map2)
    println(map3)
    println(map1 == map2)
    println(map1 == map3)
    println("---合并集合2---")
    val map_1 = mutable.Map("a" -> 1, "b" -> 2)
    val map_2 = mutable.Map("q" -> 10, "e" -> 11)

    val map_3 = map_1 ++ map_2
    println(map_1)
    println(map_2)
    println(map_3)
    println(map_1 == map_3)


    // val map4 = map1 --= map2
  }
}
