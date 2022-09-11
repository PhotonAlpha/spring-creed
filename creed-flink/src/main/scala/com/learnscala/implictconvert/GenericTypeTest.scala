package com.learnscala.implictconvert

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/7/2022 3:56 PM
 */
object GenericTypeTest {
  def main(args: Array[String]): Unit = {
    //斜变 +  ；可变成父类
    // val child: MyCollection[GrandParent] = new MyCollection[Parent]
    //逆变 -  ；可变成子类
    val child2: MyCollection[Child] = new MyCollection[Parent]
    //泛型指定上下限
    val child3 = new PersonList[GrandParent]
    // val child4 = new PersonList[Child]

  }
}

class GrandParent {

}

class Parent extends GrandParent {

}

class Child extends Parent {

}

class MyCollection[-T] {

}
class PersonList[T >: Parent] {

}
class PersonList2[T <: Parent] {

}
