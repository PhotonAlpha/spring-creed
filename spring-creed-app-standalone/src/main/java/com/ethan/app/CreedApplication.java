package com.ethan.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages = "com.ethan")
@EnableJpaRepositories("com.ethan.app.dao")
@EntityScan("com.ethan.entity")
@EnableCaching
/*@EnableWebMvc @see application配置失效yu自动配置的静态资源失效.md*/
public class CreedApplication {
  public static void main(String[] args) {
    SpringApplication.run(CreedApplication.class, args);
  }
}
