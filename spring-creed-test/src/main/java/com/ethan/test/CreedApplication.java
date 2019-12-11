package com.ethan.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.ethan")
@EnableCaching
public class CreedApplication {
  public static void main(String[] args) {
    SpringApplication.run(CreedApplication.class, args);
  }
}
