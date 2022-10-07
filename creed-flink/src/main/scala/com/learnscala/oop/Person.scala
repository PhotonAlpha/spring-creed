package com.learnscala.oop

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 11:09 AM
 */
class Person() {
  private var idCard ="UNKNOW"
  protected var name = "alice"
  var sex = "gender"
  private[learnscala] var age = 18

  println("父类的主构造器")

  def this(idCard: String) {
    this()
    this.idCard = idCard
    println("父类的辅助构造器")
  }

  def printInfo() = {
    println(s"Person($idCard, $name, $sex, $age)")
  }

}
