package com.learnscala.feature

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 5:53 PM
 */
class Student extends Person with Young {

  override val name: String = "bob"

  age = 20

  override def play(): Unit = {
    super.play()
    println(s"$name $age is playing")
  }

  override def dating(): Unit = {
    println(s"$name $age is dating")
  }

  def study() = {
    println(s"$name $age is studying")
  }

  override def sayHello(): Unit = {
    super.sayHello()
    println(s"$name $age is saying hello")
  }

  //实现最后一个
  override def eat(): Unit = super.eat()
}

object Student {
  def main(args: Array[String]): Unit = {
    val student = new Student()
    student.eat()
    student.study()
    student.sayHello()
    student.dating()
    student.play()

    println("====动态插入特征====")
    val student2 = new Student() with Talent {
      override def singing(): Unit = {
        println("I'm singing")
      }
      override def dancing(): Unit = {
        println("I'm dancing")
      }
    }

    student2.play()
    student2.dancing()
    student2.singing()

  }
}
