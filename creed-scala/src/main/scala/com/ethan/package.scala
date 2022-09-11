package com

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 10:45 AM
 */
package object ethan {
  //定义包共享的属性和方法
  val commonVal = "ethan"
  def commonMtd() = {
    println(s"common method with value $commonVal")
  }
}
