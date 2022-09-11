package com.learnscala.mapreduce

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/5/2022 5:50 PM
 */
object HighLevelFunction {
  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3, 4, 5, 6, 7, 8, 9)
    println("===取偶数===")
    // val result = list.filter(i => i % 2 == 0)
    val result = list.filter(_ % 2 == 0)
    println(result)
    //把集合中每个数*2
    val result1 = list.map(_ * 2)
    println(result1)

    println("===扁平化===")
    val list2 = List(List(1, 2, 3, 4, 5, 6, 7, 8, 9), List(10, 20, 30), List(11, 21, 31))
    println(list2.flatten)

    val list3 = List("hello world", "hello java", "hello scala")
    // list3.map(str => str.split(" "))
    //   .flatMap()
    println(list3.flatMap(_.split(" ")))

    println("===分组操作===")
    val map1 = list.groupBy(d => if (d % 2 == 0) "基数" else "偶数" )
    println(map1)
  }
}
