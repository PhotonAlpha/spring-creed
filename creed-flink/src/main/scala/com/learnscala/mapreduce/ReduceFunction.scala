package com.learnscala.mapreduce

import scala.collection.mutable

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/5/2022 5:50 PM
 */
object ReduceFunction {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    println("===reduce===")
    println(list.reduce(_ + _))
    println(list.reduceLeft(_ - _))

    // (1 - (2 - (3 - (4 - 5)))))  = 5
    println(list.reduceRight(_ - _))
    println("===fold===")
    println(list.fold(10)(_ + _))
    println(list.foldLeft(10)(_ - _))
    // (1-(2-(3-(4-(5-(6-(7-(8-(9-11)))))))))
    println(list.foldRight(11)(_ - _))

    println("===Map===")
    val map1 = Map("a" -> 1, "b" -> 3, "c" -> 6)
    val map2 = mutable.Map("a" -> 6, "b" -> 2, "c" -> 9, "d" -> 3)
    println(map1 ++ map2)

    map1.foldLeft(map2)((mergedMap, kv) => {
      val key = kv._1
      val value = kv._2
      mergedMap(key) = mergedMap.getOrElse(key, 0) + value
      mergedMap
    })
    println(map1)
    println(map2)

  }
}
