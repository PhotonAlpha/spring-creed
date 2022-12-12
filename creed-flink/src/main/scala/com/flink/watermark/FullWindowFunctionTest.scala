package com.flink.watermark

import com.flink.sourcebound.Event
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import java.lang
import java.time.{Instant, LocalDateTime}
import java.util.TimeZone

/**
 * 水位线测试
 */
object FullWindowFunctionTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    // env.getConfig.setAutoWatermarkInterval(100)
    //从元素中读取数据
    val stream = env.addSource(new ClickHouseSource)
      .assignAscendingTimestamps(_.timestamp)

    stream.keyBy(data => "key")
      .window(TumblingEventTimeWindows.of(Time.seconds(10)))
      .process(new UvCountByWindow)
      .print()

    env.execute
  }

  //用二元组表述中间聚合的状态
  class UvCountByWindow extends ProcessWindowFunction[Event, String, String, TimeWindow] {
    override def process(key: String, context: Context, elements: Iterable[Event], out: Collector[String]): Unit = {

      var userSet = Set[String]()
      elements.foreach(userSet += _.user)
      val uv = userSet.size
      // 提取窗口信息进行输出
      val startTime = context.window.getStart
      val endTime = context.window.getEnd
      val start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), TimeZone.getDefault().toZoneId());
      val end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endTime), TimeZone.getDefault().toZoneId());
      out.collect(s"窗口 $start - $end UV vlaue is $uv")
    }
  }

}
