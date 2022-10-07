package com.learnscala.feature

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/30/2022 5:51 PM
 */
trait Young {
  val name = "young"

  def play() = {
    println("playing")
  }

  def dating()

  def eat() = {
    println(s" $name is eating")
  }
}
