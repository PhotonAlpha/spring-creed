package com.learnscala.oop

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 11:16 AM
 */
object OOPTest {
  val OOP = "test"
  def main(args: Array[String]): Unit = {
    val worker = new Worker()
    println(worker.printInfo())
    println("====>")
    val dancer = new Dancer
    dancer.printInfo()
    println("+==============")
    val dancer2 = new Dancer("1234", "lili", "ssss")
    dancer2.printInfo()


  }

}
