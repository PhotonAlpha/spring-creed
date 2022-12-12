package com.flink.watermark

import com.flink.sourcebound.Event
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time

import scala.collection.mutable

/**
 * 水位线测试
 */
object AggregateWaterMarkTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // env.getConfig.setAutoWatermarkInterval(100)
    //从元素中读取数据
    val stream = env.addSource(new ClickHouseSource)
      .assignAscendingTimestamps(_.timestamp)


    //PV UV 计算
    val res = stream.keyBy(data => true)
      .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(10)))
      // b.聚合函数AggregateFunction
      .aggregate(new PvUv)
    res.print()
    env.execute
  }

  //用二元组表述中间聚合的状态
  class PvUv extends AggregateFunction[Event, (Long, mutable.Set[String]), Double] {
    //初始状态
    override def createAccumulator(): (Long, mutable.Set[String]) = {
      (0L, mutable.Set[String]())
    }

    // 每来一条数据，叠加聚合
    override def add(value: Event, acc: (Long, mutable.Set[String])): (Long, mutable.Set[String]) = {
      println(s"value $value acc: $acc")
      (acc._1 + 1, acc._2 + value.user)
    }

    override def getResult(acc: (Long, mutable.Set[String])): Double = {
      println(s"getResult:$acc")
      (acc._1.toDouble / acc._2.size)
    }

    //滑动窗口不用合并数据
    override def merge(a: (Long, mutable.Set[String]), b: (Long, mutable.Set[String])): (Long, mutable.Set[String]) = {
      println(s"a:$a b:$b")
      a
    }
  }

}
