package com.flink.watermark

import com.flink.sourcebound.Event
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.assigners.{EventTimeSessionWindows, SlidingEventTimeWindows, TumblingEventTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time

import java.time.Duration

/**
 * 水位线测试
 */
object WaterMarkTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // env.getConfig.setAutoWatermarkInterval(100)
    //从元素中读取数据
    val stream = env.addSource(new ClickHouseSource)

    //1.a.有序流的水位线生成策略
    val strategy = WatermarkStrategy.forMonotonousTimestamps[Event]()
      //指定时间戳
      .withTimestampAssigner(new SerializableTimestampAssigner[Event] {
        override def extractTimestamp(element: Event, recordTimestamp: Long): Long = element.timestamp
      })
    //1.b.乱序流的水位线生成策略
    val boundedStrategy = WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2L))
      //指定时间戳
      .withTimestampAssigner(new SerializableTimestampAssigner[Event] {
        override def extractTimestamp(element: Event, recordTimestamp: Long): Long = element.timestamp
      })
    val withStrategy = stream.assignTimestampsAndWatermarks(strategy)

    //2. 窗口
    // a.时间窗口 time window
    // b.计数窗口 count window
    // c.滚动窗口 tumbling window， 即固定大小的窗口（可以根据时间和数量）
    // d.滑动窗口 sliding window，也是固定大小，当滑动步长小于窗口大小时，就会出现重叠
    // e.会话窗口 session window，两个数据之间大于gap，就会结束并创建新的session
    // f.全局窗口 global window，全局有效的窗口，默认不会触发，需要触发一个触发器
    //按键分区窗口，keyby之后给窗口
    val res = withStrategy
      // .keyBy(_.user)
      .map(data => (data.user, data.url, 1))
      .keyBy(_._1)
      .window(TumblingEventTimeWindows.of(Time.seconds(5)))
      // .window(SlidingEventTimeWindows.of(Time.minutes(2), Time.seconds(10)))
      // .window(EventTimeSessionWindows.withGap(Time.seconds(10)))
      // .countWindow(10L)
      // .timeWindow(Time.seconds(10))
      //增量聚合
      // a.规约函数ReduceFunction 可以看成一个有窗口的聚合
      .reduce((state, date) => (date._1, date._2, date._3 + state._3))
    // b.聚合函数AggregateFunction
    //窗口分配器：WindowAssigner


    //非按键分区窗口，对所有数据开窗口

    res.print()
    env.execute
  }


}
