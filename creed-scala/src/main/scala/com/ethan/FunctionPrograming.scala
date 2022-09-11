package com.ethan

class Student2(var name: String = "xiaoming", var age: Int = 18) {

};

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/29/2022 3:42 PM
 */
object FunctionPrograming {
  def main(args: Array[String]): Unit = {

    // 定义函数
    // 1.
    def f0() = {

      val res =  () => {
        "world"
      }
      println("hi:" + res())
    }
    f0
    println("=========")
    // 匿名函数简化
   // val f1 = (name: String) => "hi:" + name

    def f(func: String => String, name: String) = {
      val str = func(name)
      println("hello:"+str)
    }
    f(name => ":my name is:" + name, "ming")

    println("===>2")

    def f2(func: String => Unit) = {
      func("how are u")
    }
    f2(println(_))
    f2(println)
    println("===>+_+1")
    //定义一个二元函数，只操作1和2
    def dualFunction(func: (Int, Int) => Int) = {
      func(1, 2)
    }

    println(dualFunction((a, b) => a - b))
    println(dualFunction(_ - _))
    //B-A就是 -a + b
    println(dualFunction((a, b) => b - a))
    println(dualFunction(-_ + _))

    println("===>+_+2")
    def f3(n: Int) = {
      println("calling f3:" + n)
      n+1
    }
    def f4() = {
      1
    }

    // 1.函数作为值传递
    val fuc1 = f3 _ // You can make this conversion explicit by writing `f3 _` or `f3(_)` instead of `f3`
    val fuc2: Int => Int  = f3 // You can make this conversion explicit by writing `f3 _` or `f3(_)` instead of `f3`
    println(fuc1(1))
    println(fuc2(2))

    val func3 = f4 _
    println(func3())
    // 2.函数作为参数传递
    
  }


}
