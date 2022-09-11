package com.learnscala

/**
 * @param name
 */
class Student(name: String, var age: Int) {

  def printInfo(): Unit = {
    println("==>name:" + name + " age:" + age + " school:" + Student.school)
  }

  def +() = {
    println("hid==>name:" + name + " age:" + age + " school:" + Student.school)
    s"hid==>name:${name} age:${age} school:${Student.school}"
  }
}

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/24/2022 5:48 PM
 */
object Student {
  val school: String = "shenzhen"

  def main(args: Array[String]): Unit = {
    // commnet
    /*commnet*/
    // var 变量 val 常量
    var abc = "abc"
    // val longStr = `${abc}`
    var _student = new Student("alice", 12)
    _student.age = 13
    _student.printInfo()
    _student.+()

    var /+# = "final"
    var `if` = "if"
    // * 可用于字符串多次拼接
    println(`if`*3)

    // %传值
    // printf("%s people %s", _student.age, `if`)

    // 字符串插值 s""
    val template = s"${_student.age} at ${`if`}"
    val num  = 2.321321
    val template2 = f"${num}%2.2f"
    println(template)
    println(template2)
    println(raw"${num}%2.2f")
    // 三引号表示字符串，表示字符串的格式输出
    // | stripMargin 表示从 | 开始顶格输出
    val template3 =
    s"""
      select * from abc
        where id = 123
      |and
    """.stripMargin
      println(template3)




  }
}
