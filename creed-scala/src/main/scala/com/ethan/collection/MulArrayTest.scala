package com.ethan.collection

/**
 * 创建二维数组
 */
object MulArrayTest {
  def main(args: Array[String]): Unit = {
    //创建二维数组
    val array = Array.ofDim[Int](2, 3)
    //访问元素
    array(0)(0) = 10
    array(0)(1) = 20
    array(0)(2) = 30
    array(1)(0) = 11
    array(1)(1) = 12
    array(1)(2) = 13
    // array(2)(0) = 21
    // array(2)(1) = 22
    // array(2)(2) = 23

    array.foreach(line => println(line.mkString(",")))

  }
}
