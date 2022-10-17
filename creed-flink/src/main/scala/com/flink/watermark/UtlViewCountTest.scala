package com.flink.watermark

import com.flink.sourcebound.Event
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import java.time.{Instant, LocalDateTime}
import java.util.TimeZone

// 定义统计输出的结构数据结构
case class UrlViewCount(url: String, count: Long, windowStart: Long, windowEnd: Long) {}
/**
 * 水位线测试
 */
object UtlViewCountTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // env.getConfig.setAutoWatermarkInterval(100)
    //从元素中读取数据
    val stream = env.addSource(new ClickHouseSource)
      .assignAscendingTimestamps(_.timestamp)

    // 结合使用 增量函数和全窗口函数，包装统计信息


    stream.keyBy(_.url)
      .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
      // .aggregate(new UrlViewCountAgg, new UrlViewCountResult)
      // .print()

    env.execute
  }


  // class UrlViewCountAgg extends AggregateFunction[Event, Long, Long] {
  //
  // }

  // class UrlViewCountResult extends
}
