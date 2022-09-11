package com.flink.batch

import org.apache.flink.api.scala._

/**
 * 1.2之后实现了流/批量一体，以下方式不再推荐
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/8/2022 2:33 PM
 */
@Deprecated
object BatchWordCount {
  def main(args: Array[String]): Unit = {
    // 1.创建一个执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment
    // 2.读取文本文件数据
    val lineDataSet = env.readTextFile("file:///workspace/PWEB/source/spring-creed/creed-flink/src/main/resources/test.txt")
    // val lineDataSet = env.readTextFile("test.txt")
    // 3.对数据进行转换处理
    println("lineDataSet:" + lineDataSet)
    val wordOne = lineDataSet.flatMap(_.split(" "))
      .map(word => (word, 1))
    println("wordOne:" + wordOne)
    // 4.按照单词进行分组
    // val wordOneGroup = wordOne.groupBy(0)
    val wordOneGroup = wordOne.groupBy(0)
    println("wordOneGroup:" + wordOneGroup)
    // 5。对分组数据进行聚合统计
    val wordOneSum = wordOneGroup.sum(1)
    println("wordOneSum:" + wordOneSum)
    // 6.打印输出结果
    wordOneSum.print()
  }
}
