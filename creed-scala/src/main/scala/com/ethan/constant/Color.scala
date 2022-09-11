package com.ethan.constant

/**
 * 定义枚举类
 */
object Color extends Enumeration {
  val MONDAY = Value(1, "Monday")
  val TUESDAY = Value(2, "Tuesday")
  val WEDNESDAY = Value("Wednesday")
}

//定义应用类
object TestApp extends App {
  println(Color.withName("Monday") == Color.MONDAY)
  println(Color.WEDNESDAY)
  println(Color.WEDNESDAY.id)
  type MyString = String
  val name: MyString = "hello"

}
