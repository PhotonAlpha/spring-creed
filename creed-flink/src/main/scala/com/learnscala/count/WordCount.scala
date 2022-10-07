package com.learnscala.count

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/6/2022 11:01 AM
 */
object WordCount {
  def main(args: Array[String]): Unit = {
    val stringList = List("hello world", "hello java", "hello scala", " hello spark")
    val wordList = stringList
      .map(_.trim)
      .flatMap(_.split(" "))

    println(wordList)
    val group = wordList.groupBy(w => w)
      .map(kv => (kv._1, kv._2.size))
    println(group)
    println("===map转list，并排序取前三===")
    val tuples = group.toList
      .sortWith(_._2 > _._2)
      .take(3)
    println(tuples)


    println("===complex===")
    val stringList2 = List(("hello", 1), ("hello world", 2), ("hello scala", 3),
      ("hello spark from scala", 1), ("hello flink from scala", 2))

    val newStringList2 = stringList2.flatMap(w =>
      w._1.split(" ")
        .map(s => (s, w._2))
    )
    println(newStringList2)
    // val stringToTuples = newStringList2.groupBy(_._1)
    // println(stringToTuples)
    val mapValues = newStringList2.groupBy(_._1)
      // .map(kv => (kv._1, kv._2.map(_._2).sum))
      .mapValues(w => w.map(_._2).sum)
    println(mapValues)
    val stringToInt = mapValues
      .toList
      .sortWith(_._2 > _._2)
    println(stringToInt)
  }
}
