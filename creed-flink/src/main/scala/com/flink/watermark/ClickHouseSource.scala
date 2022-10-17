package com.flink.watermark

import com.flink.sourcebound.Event
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.watermark.Watermark

import java.util.concurrent.TimeUnit
import scala.util.Random

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/22/2022 4:43 PM
 */
class ClickHouseSource extends SourceFunction[Event] {
  var running = true

  override def run(ctx: SourceFunction.SourceContext[Event]): Unit = {
    val random = new Random()
    val users = Array("Alice", "Mary", "Bob", "Cary", "Danny")
    val urls = Array("./home", "./cart", "./product?id=1", "./product?id=2", "./product?id=3")
    while (running) {
      val event = Event(users(random.nextInt(users.length)), urls(random.nextInt(urls.length)), System.currentTimeMillis())
      //为自定义数据源中发送的数据分配时间戳
      // ctx.collectWithTimestamp(event, event.timestamp)
      // 插入时间戳
      // ctx.emitWatermark(new Watermark(event.timestamp - 1L))

      ctx.collect(event)
      TimeUnit.SECONDS.sleep(1)
    }
  }

  override def cancel(): Unit = this.running = false
}
