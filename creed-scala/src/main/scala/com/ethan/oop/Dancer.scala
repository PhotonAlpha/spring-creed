package com.ethan.oop

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 11:12 AM
 */
class Dancer(var idCard: String) extends Person(idCard: String) { //主构造器
  var address: String = _
  var title: String = _

  println("子类的主构造器")

  def this() {
    this("default")
    println("子类的辅助造器")
  }

  def this(idCard:String, title: String, address: String) {//辅助构造器,必须调用主构造器
    this(idCard)
    this.title = title
    this.address = address
    println("子类的辅助造器")
  }
  override def printInfo(): Unit = {
    println(s"Dancer($address, $title, $idCard, $age, $address, $name)")
  }
}

class Dancer2(var title: String, var address: String) extends Person