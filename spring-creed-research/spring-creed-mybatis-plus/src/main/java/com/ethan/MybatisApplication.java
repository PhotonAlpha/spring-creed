package com.ethan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class
})
@MapperScan("com.ethan.mapper")
public class MybatisApplication {
  public static void main(String[] args) {
    SpringApplication.run(MybatisApplication.class, args);
  }
}
