package com.flink.stream

import org.apache.flink.streaming.api.scala._


/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/8/2022 4:35 PM
 */
object BoundedStreamWordCount {
  def main(args: Array[String]): Unit = {
    // 1.创建一个执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // 2.读取文本文件数据
    val lineDataStream = env.readTextFile("file:///workspace/PWEB/source/spring-creed/creed-flink/src/main/resources/test.txt")
    // 3.对数据进行转换处理
    val wordOne = lineDataStream.flatMap(_.split(" "))
      .map(word => (word, 1))
    println("wordOne:" + wordOne)
    // 4.按照单词进行分组
    val wordOneGroup = wordOne.keyBy(_._1)
    println("wordOneGroup:" + wordOneGroup)
    // 5。对分组数据进行聚合统计
    val wordOneSum = wordOneGroup.sum(1)
    println("wordOneSum:" + wordOneSum)
    // 6.打印输出结果
    wordOneSum.print()
    // 7.执行当前任务
    env.execute()
  }
}
