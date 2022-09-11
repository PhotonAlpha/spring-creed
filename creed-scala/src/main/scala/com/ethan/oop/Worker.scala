package com.ethan.oop

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 11:12 AM
 */
class Worker extends Person {
  var idCard ="123456"
  override def printInfo() = {
    println(s"Person($name, $sex, $age)")
    name = "bob"
    sex = "woman"
    age = 19
    println(s"Worker($idCard, $name, $sex, $age)")
  }
}
