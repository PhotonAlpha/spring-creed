package com.ethan.test;

import com.ethan.test.service.Person;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication
//@EnableCaching
public class CreedTestApplication {
  public static void main(String[] args) {
    ConfigurableApplicationContext ac = SpringApplication.run(CreedTestApplication.class, args);

//    System.out.println("xml加载完毕");
//    Person person1 = (Person) ac.getBean("person");
//    System.out.println(person1);
//    System.out.println("关闭容器");
//    ac.close();
  }
}
