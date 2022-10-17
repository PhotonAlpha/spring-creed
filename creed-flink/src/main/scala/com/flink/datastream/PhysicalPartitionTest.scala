package com.flink.datastream

import org.apache.flink.api.common.functions.Partitioner
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.functions.source._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/20/2022 4:43 PM
 */
object PhysicalPartitionTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // env.addSource(new SourceFunction[Event] {
    //   override def run(ctx: SourceFunction.SourceContext[Event]): Unit = ???
    //
    //   override def cancel(): Unit = ???
    // })
    val stream = env.addSource(new RichParallelSourceFunction[Int] {
      override def run(ctx: SourceFunction.SourceContext[Int]): Unit = {
        for (i <- 0 to 7) {
          //通过上下文获取分区
          println(s"IndexOfThisSubtask:${getRuntimeContext.getIndexOfThisSubtask}")
          if (getRuntimeContext.getIndexOfThisSubtask == (i + 1) % 2) {
            ctx.collect(i + 1)
          }
        }
      }

      override def cancel(): Unit = ???
    }).setParallelism(2)
    // 3. 物理分区算子
    // 避免数据倾斜

    // a. 随机分区
    // stream.shuffle
    //   .print("shuffle").setParallelism(4)
    // b. 轮询分区
    // stream.rebalance
    //   .print("rebalance").setParallelism(4)
    // c. 重缩放
    // stream.rescale
    //   .print("rescale").setParallelism(4)
    // stream.rebalance
    //   .print("rebalance").setParallelism(4)

    // 自定义分区
    stream.partitionCustom(new Partitioner[Int] {
      override def partition(key: Int, numPartitions: Int): Int = (key + 1) % 2
    }, date => date).print("partitionCustom").setParallelism(4)

    env.execute
  }
}
