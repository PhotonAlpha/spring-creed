package com.learnscala.exceptionhandler

import java.io.IOException
import scala.util.control.Breaks
import scala.util.control.Breaks.{break, breakable, tryBreakable}

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/7/2022 2:08 PM
 */
object ExceptionTest {
  def main(args: Array[String]): Unit = {
    // try {
    //   var res = 100 / 0
    // } catch {
    //   case e: ArithmeticException => throw new IOException("no"+e.getMessage)
    //   case e: Exception => throw e
    // } finally {
    //   println("finally")
    // }
    println("===break实现===")
    breakable {
      for (i <- 0 to 10) {
        println("-->i:" + i)
        if (i == 5) {
          break
        }
      }
    }
    tryBreakable {
      for (i <- 0 to 10) {
        println("-->i:" + i)
        if (i == 5) {
          break
        }
      }
    } catchBreak {
      // case e => println(s"----${e}")
      println(s"get ex")
    }



    println("===continue实现===")
    for (i <- 0 to 10) {
      breakable{
        if (i == 5) {
          break
        }
        println("-->i:" + i)
      }
    }
  }



}
