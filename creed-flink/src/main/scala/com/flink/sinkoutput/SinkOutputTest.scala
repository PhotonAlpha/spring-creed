package com.flink.sinkoutput

import com.flink.sourcebound.Event
import org.apache.flink.api.common.serialization.{SimpleStringEncoder, SimpleStringSchema}
import org.apache.flink.connector.kafka.sink.{KafkaSink, KafkaSinkBuilder}
import org.apache.flink.core.fs.Path
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaProducer, FlinkKafkaProducerBase}

/**
 * 1. 基本转换算子
 * 2. 聚合算子
 * 3. 物理分区算子
 */
object SinkOutputTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(4)
    //从元素中读取数据
    val stream = env.fromElements(Event("alice", "./home", 1663571365607L),
      Event("bob", "./cart", 1663571365603L),
      Event("bob", "./list", 1663571365603L),
      Event("bob", "./prod?id=1", 1663571365602L),
      Event("cindy", "./prod?id=1", 1663571365602L),
      Event("danny", "./prod?id=1", 1663571365602L),
      Event("danny", "./prod?id=1", 1663571365602L),
      Event("danny", "./prod?id=1", 1663571365602L),
      Event("danny", "./prod?id=1", 1663571365602L),
      Event("danny", "./prod?id=1", 1663571365602L),
      Event("cindy", "./list", 1663571365606L))

    val fileSink = StreamingFileSink.forRowFormat(new Path("./out"),
      new SimpleStringEncoder[String]("UTF-8"))
    // withRollingPolicy(
      // DefaultRollingPolicy.builder()
        // .withMaxPartSize()
        // .withRolloverInterval()
        // .withInactivityInterval()
    // )
    .build


    stream.map(_.toString).addSink(fileSink)

    // val kafkaSink = new KafkaSinkBuilder()
    //   .setBootstrapServers("hadoop:102")
    //   // .setKafkaProducerConfig()
    //   .setRecordSerializer(new SimpleStringSchema)
    //   .build()

    env.execute
  }


}
