package com.outter

import com.ethan.oop.Worker

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 11:16 AM
 */
object OOPTest2 {
  def main(args: Array[String]): Unit = {
    val worker = new Worker()
    worker.idCard
    worker.sex
    // worker.age
    println(worker.printInfo())
  }

}
