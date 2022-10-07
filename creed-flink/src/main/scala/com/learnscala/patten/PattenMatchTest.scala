package com.learnscala.patten

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/6/2022 4:25 PM
 */
object PattenMatchTest {
  def main(args: Array[String]): Unit = {
    val operator = '+'
    val a = 10
    val b = 20

    val value = operator match {
      case '+' => a + b
      case '-' => a - b
      case '*' => a * b
      case '/' => a / b
      case _ => "illegal"
    }
    println(s"value:$value")
    println("===模式守卫【求一个整数的绝对值】===")

    def abs(num: Int): Int = {
      num match {
        case i if i >= 0 => i
        case i if i < 0 => -i
      }
    }

    println(abs(1))
    println(abs(0))
    println(abs(-100))

    println("===模式守卫【匹配不同的类型】===")

    def describeConst(x: Any): String = {
      x match {
        case 1 => "Int"
        case "hello" => "String"
        case true => "Boolean"
        case '+' => "Char +"
        case _ => "UNKNOW"
      }
    }

    println(describeConst("a"))

    println("===模式守卫【匹配类型】===")

    def describeType(x: Any): String = {
      x match {
        case i: Int => "Int" + i
        case s: String => "String" + s
        case i: Boolean => "Boolean" + i
        case j: Char => "Char" + j
        case j: List[String] => "List[String]" + j
        case j: Array[Int] => "Array[Int]" + j.mkString(",")
        case a => "something else " + a
      }
    }

    println(describeType(Array(1, 2, 3)))
    println(describeType(Array("1", "2", "3")))
    println(describeType(List("1", "2", "3")))
    println(describeType(Map(1 -> "a")))


    println("===模式守卫【匹配集合类型】===")

    for (arr <- List(
      Array(0),
      Array(1, 0),
      Array(0, 12),
      Array(0, 1, 0),
      Array(1, 1, 0),
      Array("hello", 2, 3)
    )) {
      val result = arr match {
        case Array(0) => "0"
        case Array(1, 0) => "1,0"
        // case Array(0, 1, 0) => "0,1,0"
        case Array(0, _*) => "以0开头的数组"
        case Array(x, 1, z) => s"$x - 1 - $z"
        case Array(x, y, z) => s"$x - $y - $z"
        case _ => "something else"
      }
      println(result)
    }


    println("===模式守卫【匹配列表类型】===")
    for (arr <- List(
      List(0),
      List(1, 0),
      List(0, 12),
      List(0, 1, 0),
      List(1, 1, 0),
      List("hello", 2, 3)
    )) {
      val result = arr match {
        case List(0) => "0"
        case List(1, 0) => "1,0"
        case List(0, _) => "以0开头的数组" //_*表示参数可变
        case List(0, _*) => "以0开头的数组不定长度列表" //_*表示参数可变
        case List(x, 1, z) => s"$x - 1 - $z"
        case List(x, y, z) => s"$x - $y - $z"
        case _ => "something else"
      }
      println(result)
    }


    println("===模式守卫【列表匹配方式二】===")
    // val list2 = List(1, 1, 0, 23, 77)
    val list2 = List(1)
    val str = list2 match {
      case first :: second :: rest => s"$first $second $rest"
      case _ => "something else"
    }
    println(str)


    println("===模式守卫【元组匹配】===")
    for (tuple <- List(
      (0),
      (1, 0),
      (0, 12),
      (0, 1, 0),
      (1, 1, 0),
      ("hello", 2, 3)
    )) {
      val result = tuple match {
        case (a, b) => s"二元组：$a $b"
        case (0, _) => "以0开头的数组"
        case (a, 1, c) => s"san元组：$a 1 $c"
        case (x, y, z) => s"san元组：$x $y $z"
        case _ => "something else"
      }
      println(result)
    }

    println("===模式匹配赋值【元组匹配:扩展】===")
    val (x, y) = (10, "hello")
    println(s"$x $y")

    val fir :: sec :: res = List(23, 15, 33, 19, 22)
    val List(fir1, sec1, res1) = List(23, 15, 33)
    println(s"$fir1 $sec1 $res1")
    println(s"$fir $sec $res")

    println("===for中实现模式匹配赋值【元组匹配:扩展】===")
    val list3 = List(("a", 12), ("b", 3), ("c", 10), ("a", 99))
    for ((k, v) <- list3) {
      println(s"for中实现模式匹配赋值=$k $v")
    }
    //不考虑某个位置的变量，只遍历key或者value
    for ((w, _) <- list3) {
      println(s"key is=$w")
    }
    //指定某个位置的值
    for (("a", count) <- list3) {
      println(s"a value=$count")
    }
  }
}
