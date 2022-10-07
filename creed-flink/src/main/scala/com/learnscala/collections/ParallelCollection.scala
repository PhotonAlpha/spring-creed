package com.learnscala.collections

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/6/2022 3:56 PM
 */
object ParallelCollection {
  def main(args: Array[String]): Unit = {
    val indexedSeq = (1 to 100).map(x => Thread.currentThread().getName)
    println(indexedSeq)
    println("---并行集合----")
    val indexedSeq2 = (1 to 100).par.map(x => Thread.currentThread().getName)
    println(indexedSeq2)
  }
}
