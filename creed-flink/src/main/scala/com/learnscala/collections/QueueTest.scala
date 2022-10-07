package com.learnscala.collections

import scala.collection.immutable.Queue
import scala.collection.mutable

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/6/2022 2:37 PM
 */
object QueueTest {
  def main(args: Array[String]): Unit = {
    // 创建一个可变队列
    val queue = mutable.Queue("a", "b", "c")
    println(queue)
    queue.enqueue("d")
    println(queue.dequeue)
    println(queue)
    println(queue.dequeue)
    println(queue)
    println(queue.dequeue)
    println(queue)

    println("---immutable.Queue----")
    val queue2 = Queue("a", "b", "c")
    val queue3   = queue2.enqueue("d")
    println(queue2)
    val dequeue1 = queue2.dequeue
    println(dequeue1)
    queue2.enqueue("d")
    println(queue2)
    println(queue2.dequeue)
    println(queue2)
    println(queue2.dequeue)
    println(queue2)




  }
}
