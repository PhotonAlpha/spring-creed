package com.learnscala.oop

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 5:31 PM
 */
class Singleton  private(val name: String, val age: Int) {
  def printInfo() {
    println(s"name=$name age=$age school=${Student11.school}")
  }
}

object Singleton {
  val school = "abc"
  private var singleton: Singleton = _
  def getInstance(name: String, age: Int) = {
    if(singleton == null) {
      singleton = new Singleton("alice", 18)
    }
    singleton
  }
  def apply(name: String, age: Int) = {
    if(singleton == null) {
      singleton = new Singleton("alice", 18)
    }
    singleton
  }
}

object Test_Singleton {
  def main(args: Array[String]): Unit = {
    val a = Singleton("a", 18)
    val b = Singleton("a", 18)
    val c = Singleton("a", 18)
    println(a)
    println(b)
    println(c)
  }
}
