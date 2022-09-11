package com.ethan

import scala.beans.BeanProperty

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 10:35 AM
 */
object ObjectOrientedTest {
  def main(args: Array[String]): Unit = {
    commonMtd()

    val test = new ObjectOrientedTest()
    println(test)
    test.age = 19
    test.setName("bob")
    test.sex = "gender"
    println(test)

  }
}

class ObjectOrientedTest {
  @BeanProperty
  var name = "alice"
  private var age = 18
  private var sex:String = _


  override def toString = s"ObjectOrientedTest($name, $age, $sex)"
}
