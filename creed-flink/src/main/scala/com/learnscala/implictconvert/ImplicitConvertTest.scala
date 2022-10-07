package com.learnscala.implictconvert

/**
 * 隐式函数
 *
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/7/2022 3:30 PM
 */
object ImplicitConvertTest {
  def main(args: Array[String]): Unit = {
    // 隐式函数
    println("===隐式函数===")
    implicit def convert(num: Int): MyRichInt = new MyRichInt(num)
    println(12 myMax 15)

    println("===隐式类===")
    println(11 myMin2 15)

    println("===隐式参数===")
    //是从上下文找类型 而不是参数名【同一作用域内只能有一种类型】
    implicit val str = "alice";
    def sayHello(implicit name: String = "bob") = {
      println("hello " + name)
    }
    sayHello
    //简写
    implicit val age = 20;
    def hi = {
      println("hello " + implicitly[Int])
    }
    hi
  }
  //只能定义在 【对象】或者【类】的内部
  implicit class MyRichInt2(val self: Int) {
    def myMax2(n: Int) = if (n < self) self else n

    def myMin2(n: Int) = if (n < self) n else self
  }
}

// 自定义类
class MyRichInt(val self: Int) {
  def myMax(n: Int) = if (n < self) self else n

  def myMin(n: Int) = if (n < self) n else self
}


// 隐式参数
// 隐式方法