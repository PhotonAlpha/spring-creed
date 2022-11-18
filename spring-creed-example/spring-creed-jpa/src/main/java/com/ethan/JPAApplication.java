package com.ethan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(exclude = {
    SecurityAutoConfiguration.class
})
@EntityScan("com.ethan.entity")
@EnableJpaRepositories("com.ethan.dao")
@EnableRetry
public class JPAApplication {
  public static void main(String[] args) {
    SpringApplication.run(JPAApplication.class, args);
  }
}
