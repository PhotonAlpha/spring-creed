package com.ethan

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 9:52 AM
 */
object ArrayHandler {
  def main(args: Array[String]): Unit = {
    val arr = Array(12, 45, 75, 98)

    //对数组进行处理，处理完成之后返回一个新的数组
    def arrayOp(array: Array[Int], op: Int => Int): Array[Int] = {
      for (ele <- array) yield op(ele)
    }

    def addOne(ele: Int) = {
      ele + 1
    }

    println(arrayOp(arr, addOne).mkString("Array(", ", ", ")"))
    // lazy懒加载, 将函数过程变成懒加载过程
    def sum(a: Int, b: Int) = {
      println("3.调用")
      a+b
    }
    lazy val res = sum(13, 47)
    println("1.函数调用")
    println("2.result=" + res)
    println("4.result=" + res)

  }
}
