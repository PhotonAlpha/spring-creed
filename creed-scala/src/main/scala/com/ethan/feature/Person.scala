package com.ethan.feature

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 5:40 PM
 */
trait Person {
  val name = "person"
  var age = 18
  def sayHello() = {
    println(s"Hello $name $age")
  }
  def eat() = {
    println(s" $name $age is eating")
  }
}
