package com.ethan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class
})
@EntityScan("com.ethan.entity")
public class JPAApplication {
  public static void main(String[] args) {
    SpringApplication.run(JPAApplication.class, args);
  }
}
