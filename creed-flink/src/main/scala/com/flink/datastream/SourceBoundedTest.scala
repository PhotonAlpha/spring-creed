package com.flink.datastream

import com.flink.sourcebound.Event
import org.apache.flink.api.common.functions.{FilterFunction, FlatMapFunction, MapFunction, ReduceFunction, RichMapFunction}
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.util.Collector

/**
 * 1. 基本转换算子
 * 2. 聚合算子
 * 3. 物理分区算子
 */
object SourceBoundedTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
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
    // 基本转换算子
    // 1.映射MAP
    //使用匿名函数
    val map1 = stream.map(_.user)
    // map1.print("1")
    val map2 = stream.map(new UserExtractor)
    // map2.print("2")
    val filter1 = stream.filter(u => "alice" == u.user)
    // filter1.print("3")
    val filter2 = stream.filter(new UserFilter)
    // filter2.print("4")
    val flatMap1 = stream.flatMap(new UserFlatMap)
    // flatMap1.print("5")


    // 2. 聚合算子
    // keyby之后一定会在一个分区
    val keyby1 = stream.keyBy(new UserKeySelector)
    val keyBy2 = stream.keyBy(_.user)
    // keyby1.print("6")
    // keyBy2.print("7")

    //max只会修改当前数值，其他字段会保留初始的值。
    // maxBy会修改整条数据的值
    val minBy1 = keyBy2.min("timestamp")
    // minBy1.print("8")
    /* ----------------
    8> Event(alice,./home,1663571365607)
    8> Event(bob,./cart,1663571365603)
    8> `Event(bob,./cart,1663571365602)`
    8> Event(cindy,./list,1663571365606)
     --------------------- */
    val minBy2 = keyBy2.minBy("timestamp")
    // minBy2.print("8")
    /* ----------------
    8> Event(alice,./home,1663571365607)
    8> Event(bob,./cart,1663571365603)
    8> `Event(bob,./prod?id=1,1663571365602)`
    8> Event(cindy,./list,1663571365606)
     --------------------- */
    // val maxBy1 = keyBy2.max("timestamp")
    // maxBy1.print("9")

    //reduce聚合
    val reduce1 = stream
      .map(data => (data.user, 1L))
      .keyBy(_._1)
      .reduce(new UserReduce)
    // .keyBy(data => true) //将用户设在同一个分组
    // .reduce((state, data) => if (data._2 > state._2) data else state)//取活跃度最大
    // reduce1.print("10")

    val richMap1 = stream.map(new UserRichFunction)
    // richMap1.print("11")

    env.execute
  }

  class UserExtractor extends MapFunction[Event, String] {
    override def map(t: Event): String = t.user
  }

  class UserFilter extends FilterFunction[Event] {
    override def filter(t: Event): Boolean = "alice".equals(t.user)
  }

  class UserFlatMap extends FlatMapFunction[Event, String] {
    override def flatMap(t: Event, collector: Collector[String]): Unit = {
      //如果当前是alice直接输出
      if ("alice" == t.user) {
        collector.collect(t.user)
      } else if ("bob" == t.user) {
        //如果当前是bob输出user和url
        collector.collect(t.user)
        collector.collect(t.url)
      } else {
        collector.collect(t.user)
        collector.collect(t.url)
        collector.collect(t.timestamp + "")
      }
    }
  }

  class UserKeySelector extends KeySelector[Event, String] {
    override def getKey(t: Event): String = t.user
  }

  //统计用户活跃度
  class UserReduce extends ReduceFunction[(String, Long)] {
    override def reduce(t1: (String, Long), t2: (String, Long)): (String, Long) = (t2._1, t1._2 + t2._2)
  }

  class UserRichFunction extends RichMapFunction[Event, Long] {
    override def map(t: Event): Long = t.timestamp

    override def open(parameters: Configuration): Unit = {
      println(s"Task index num ${getRuntimeContext.getIndexOfThisSubtask} is running")
    }

    override def close(): Unit = {
      println(s"Task index num ${getRuntimeContext.getIndexOfThisSubtask} is ended")
    }
  }
}
