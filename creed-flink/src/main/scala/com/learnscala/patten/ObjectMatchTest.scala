package com.learnscala.patten

/*class Teacher(val name: String, val age: Int) {

}

object Teacher {
  //用来对对象属性进行拆解
  def unapply(teacher: Teacher): Option[(String, Int)] = {
    if (teacher == null) {
      None
    } else {
      Some(teacher.name, teacher.age)
    }
  }

  def apply(name: String, age: Int) = new Teacher(name, age)
}*/

case class Teacher(name: String, age: Int)

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/6/2022 4:25 PM
 */
object ObjectMatchTest {
  def main(args: Array[String]): Unit = {
    val teacher = Teacher("alice", 19)
    // val teacher = new Teacher("alice", 18)
    val maybeString = Option(teacher).map(_.name).getOrElse("default")
    println(maybeString)

    val result = teacher match {
      case Teacher("alice", 19) => "Alice 18"
      case _ => "else person"
    }
    println(result)

  println("===偏函数===")
    //偏函数求绝对值
    // 对正负数分为不同情况
    val positiveAbs: PartialFunction[Int, Int] = {
      case x if x > 0 => x
    }
    val negativeAbs: PartialFunction[Int, Int] = {
      case x if x < 0 => -x
    }
    val zeroAbs: PartialFunction[Int, Int] = {
      case 0 => 0
    }
    def abs(x : Int): Int = (positiveAbs orElse negativeAbs orElse zeroAbs) (x)

    // println(positiveAbs(-76))
    println(abs(-76))
    println(abs(33))
    println(abs(0))
  }
}
