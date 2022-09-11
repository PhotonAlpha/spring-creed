package com.ethan.oop

//构造方法私有化
class Student11 private(val name: String, val age: Int) {
  def printInfo() {
    println(s"name=$name age=$age school=${Student11.school}")
  }
}

object Student11 {
  val school = "abc"
  def getInstance(name: String, age: Int) = new Student11(name, age)
  def apply(name: String, age: Int) = new Student11(name, age)
}

object Test11 {
  def main(args: Array[String]): Unit = {
    // val student1 = new Student11("alice", 18)
    // student1.printInfo()
    var student1 = Student11.getInstance("alice", 18)
    student1.printInfo()
    student1 = Student11("bob", 20)
    student1.printInfo()
  }
}
