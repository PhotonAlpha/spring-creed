package com.ethan.collection

import scala.collection.mutable.ArrayBuffer

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/31/2022 10:45 AM
 */
object MutableArrayTest {
  def main(args: Array[String]): Unit = {
    val arr = new ArrayBuffer[Int]()
    val arr2 = ArrayBuffer[Int](21, 32 ,13)
    // arr(0) = 11
    // println(arr(0)) error

    // 返回不可变arr，适用于不可变arr
    // val newArr = arr :+ 11
    //添加元素
    // arr += 11
    arr.append(36)
    println(arr)
    //往前追加
    // 99 +=: arr
    arr.prepend(88)
    println(arr)
    //从index1开始删除2个
    // arr.remove(1, 2)


    arr.insertAll(1, Array[Int](99, 2 ,33))
    println(arr)

    //删除指定元素
    arr -= 2
    println(arr)

    //可变转不可变
    val newArray = arr.toArray
    println(newArray.mkString(","))

    val newBuffer = newArray.toBuffer
    println(newBuffer)

    // println(newArr)
    println(arr2.toString())
  }
}
