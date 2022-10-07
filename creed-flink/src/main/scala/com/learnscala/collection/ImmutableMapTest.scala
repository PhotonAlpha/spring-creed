package com.learnscala.collection

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/31/2022 10:45 AM
 */
object ImmutableMapTest {
  def main(args: Array[String]): Unit = {
    val map1 = Map("a" -> 1, "b" -> 2)
    println(map1)
    println(map1.getClass)
    //遍历元素
    // map1.foreach((kv:( String, Int)) => println(kv))
    for (key <- map1) {
      println(s"key:${key._1} value:${key._2}")
    }
    /*println(s"val:${map1.get("c").get}")*/
    println(s"val:${map1.getOrElse("c", -1)}")
    // println(map1("c"))
  }
}
