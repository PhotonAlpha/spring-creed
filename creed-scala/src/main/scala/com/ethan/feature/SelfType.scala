package com.ethan.feature

/**
 * 自身类型
 */
object SelfType {
  def main(args: Array[String]): Unit = {
    val user = new RegisterUser("ming", "pwd")
    user.insert()

    println(user.isInstanceOf[User])// 判断类型
    println(user.isInstanceOf[RegisterUser])// 判断类型
    println(user.isInstanceOf[UserDao])// 判断类型
    println("===>")
    val dao = user.asInstanceOf[UserDao]
    val user2 = user.asInstanceOf[User]
    println(s"${user2.username} ${user2.password}")//转换类型
    dao.insert()//转换类型
    println("===>")
    println(classOf[RegisterUser])//获取
  }
}


class User(val username: String, val password: String)

trait UserDao {
  //类似于依赖注入
  _: User =>

  //向数据库插入数据
  def insert() = {
    println(s"insert into DB: ${this.username} with ${this.password}")
  }
}

class RegisterUser(username: String, password: String) extends User(username, password) with UserDao
