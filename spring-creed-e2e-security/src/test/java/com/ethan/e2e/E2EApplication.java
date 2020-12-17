package com.ethan.e2e;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class E2EApplication {
  public static void main(String[] args) {
    SpringApplication.run(E2EApplication.class, args);
  }
}
