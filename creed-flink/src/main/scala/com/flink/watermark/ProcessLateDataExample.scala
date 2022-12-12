package com.flink.watermark

import com.flink.sourcebound.Event
import org.apache.flink.api.common.eventtime.{SerializableTimestampAssigner, WatermarkStrategy}
import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.common.typeutils.base.StringSerializer
import org.apache.flink.api.scala._
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.streaming.api.scala.function.{ProcessWindowFunction, WindowFunction}
import org.apache.flink.streaming.api.scala.{OutputTag, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.assigners.{SlidingEventTimeWindows, TumblingEventTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.util.Collector

import java.time.Duration
import java.util.Properties

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 10/14/2022 5:17 PM
 */
object ProcessLateDataExample {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    // read from kafka
    // val properties = new Properties
    // properties.setProperty("bootstrap.servers", "127.0.0.1:9092")
    // properties.setProperty("auto.offset.reset", "latest")
    // properties.setProperty("group.id", "common-group")
    // properties.setProperty("enable.auto.commit", "true")
    // properties.setProperty("key.deserializer", "127.0.0.1:9092")
    // val kafkaSource = KafkaSource.builder[String]().setTopics("flinkq")
    //   .setProperties(properties)
    //   .setValueOnlyDeserializer(new SimpleStringSchema)
    //   .build()

    val kafkaSource = KafkaSource
      .builder[String]()
      .setBootstrapServers("127.0.0.1:9092")
      .setGroupId("common-group")
      .setTopics("flinkq")
      .setValueOnlyDeserializer(new SimpleStringSchema)
      .setStartingOffsets(OffsetsInitializer.latest())
      .build();


    val stream = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source")
      .map(data => {
        val fields = data.split(",")
        Event(fields(0).trim, fields(1).trim, fields(2).trim.toLong)
      })
      .assignTimestampsAndWatermarks(
        WatermarkStrategy.forBoundedOutOfOrderness[Event](Duration.ofSeconds(5))
          .withTimestampAssigner(
            new SerializableTimestampAssigner[Event] {
              override def extractTimestamp(element: Event, recordTimestamp: Long): Long = element.timestamp
            }
          )
      )
    //定义一个侧输出流的输出标签
    val outputTag = OutputTag[Event]("late-date")

    val result = stream.keyBy(_.url)
      // .window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)))
      .window(TumblingEventTimeWindows.of(Time.seconds(10)))
      //指定允许等待时间
      .allowedLateness(Time.seconds(30))
      //将迟到数据给侧输出流
      .sideOutputLateData(outputTag)
      .aggregate(new UrlViewCountAgg, new UrlViewCountResult)

    result.print("result")
    stream.print("original")
    result.getSideOutput(outputTag).print("late data")
    env.execute

    /**
      Mary,./home,1000
      Mary,./home,2000
      Mary,./home,1000
      Mary,./home,1500
      Mary,./home,1000
      Mary,./home,5000
      Mary,./home,8000
      Mary,./home,12000
      Mary,./home,70000
     */
  }

  class UrlViewCountAgg extends AggregateFunction[Event, Long, Long] {
    override def createAccumulator(): Long = 0L

    override def add(value: Event, accumulator: Long): Long = accumulator + 1

    override def getResult(accumulator: Long): Long = accumulator

    override def merge(a: Long, b: Long): Long = ???
  }

  class UrlViewCountResult extends ProcessWindowFunction[Long, UrlViewCount, String, TimeWindow] {
    override def process(key: String, context: Context, elements: Iterable[Long], out: Collector[UrlViewCount]): Unit = {
      // 提取需要的数据
      val count = elements.iterator.next
      var start = context.window.getStart
      var end = context.window.getEnd
      //输出数据
      out.collect(UrlViewCount(key, count, start, end))
    }
  }
}
