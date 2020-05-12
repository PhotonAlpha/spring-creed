package com.ethan.gradation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class GradationApplication {
  public static void main(String[] args) {
    SpringApplication.run(GradationApplication.class, args);
  }
}
