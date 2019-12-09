package com.ethan.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = "com.ethan")
@EntityScan("com.ethan.model")
@EnableCaching
public class CreedApplication {
  public static void main(String[] args) {
    SpringApplication.run(CreedApplication.class, args);
  }
}
