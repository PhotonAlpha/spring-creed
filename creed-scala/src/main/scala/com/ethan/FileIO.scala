package com.ethan

import scala.io.{Codec, Source}
import scala.util.control.Breaks
import scala.util.control.Breaks.{break, breakable}

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/25/2022 10:14 AM
 */
object FileIO {
  def main(args: Array[String]): Unit = {
    // Source.fromResource("test.txt").foreach(print)
    //将数据写入文件

    val b3 = (10 + 1).toByte
    println(b3)


    def +() = {
      // var n = 2
      // if (n > 1) {
      "hello"

      // } else {
      "hello2"
      // }

    }


    // val n: Int = null
    // println(n)
    val age = 1
    val res = if (age > 1) "people" else "baby"
    println(res)

    for (i <- 1 to 10) { // 1.to(10)
      println(i + ".hello")
    }
    for (i <- 1 until 10 if i != 5) { // 1.to(10)
      println(i + ".world")
    }

    for (i <- 1 to 10 by 2 reverse) { // 1.to(10)
      println("step " + i + ".hello")
    }
    //循环嵌套, 花括号可以换行
    /* for (i <- 1 to 10; j = i) {
      println(s"i = $i , j = $j")
    }*/
    for {i <- 1 to 10
         j = i} {
      println(s"i = $i , j = $j")
    }

    //2*n - 1, 第9层 2n-1
    for (i <- 1 to 9; star = 2 * i - 1; space = 9 - i) {
      println(" " * space + "*" * star)
    }
    // for 返回值
    val forRes = for (i <- 1 to 10) yield i
    println("forRes:" + forRes)


    //循环 break
    // 可以使用scala中的Breaks类中的break来实现
    /*try {
      for (i <- 1 to 10) {
        if (i== 3) throw  new RuntimeException
        println(i)
      }
    } catch {
      case e: Exception => //什么都不做，只是退出循环
    }*/
    breakable(
      for (i <- 1 to 10) {
        if (i== 3)
          break()
        println(s"Breaks $i")
      }
    )

  }
}
