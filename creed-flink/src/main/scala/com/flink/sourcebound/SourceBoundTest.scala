package com.flink.sourcebound

import org.apache.flink.streaming.api.scala._

import java.util.Date

/**
 * 样例类
 */
case class Event(user: String, url: String, timestamp: Long)

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/19/2022 2:59 PM
 */
object SourceBoundTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // env.setParallelism(1)
    //1. 从元素中获取数据
    val stream = env.fromElements(Event("alice", "./home", 1663571365607L),
      Event("bob", "./cart", 1663571365607L),
      Event("cindy", "./list", 1663571365607L)
    )
    //2.从集合中获取数据
    val clicks = List(Event("alice", "./home", 1663571365607L),
      Event("bob", "./cart", 1663571365607L),
      Event("cindy", "./list", 1663571365607L))
    val stream2 = env.fromCollection(clicks)

    // env.readTextFile

    stream.print("1")
    stream2.print("2")

    env.execute
  }
}
