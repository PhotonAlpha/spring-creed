package com.flink.datastream

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/9/2022 5:41 PM
 */
object SourceBoundedTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    //从元素中读取数据
    val stream = env.fromElements(1, 2, 3, 4, 5)

  }
}
