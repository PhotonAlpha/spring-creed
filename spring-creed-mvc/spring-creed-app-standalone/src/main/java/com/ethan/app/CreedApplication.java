package com.ethan.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication(scanBasePackages = "com.ethan")
@EnableJpaRepositories("com.ethan.app.dao")
@EntityScan("com.ethan.entity")
@EnableCaching
//@EnableScheduling
/*@EnableWebMvc @see application配置失效yu自动配置的静态资源失效.md*/
public class CreedApplication {
  public static void main(String[] args) {
    SpringApplication.run(CreedApplication.class, args);
  }

/*  @Scheduled(fixedRate = 1000)
  public void executed() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    System.out.println("--------------------start:" + LocalDateTime.now().format(formatter));
    System.out.println(Thread.currentThread().getName() + "start");
    //try {
      //Thread.sleep(3000L);
      System.out.println("executed thread end" + Thread.currentThread().getName());
      System.out.println("end:" + LocalDateTime.now().format(formatter));
    //} catch (InterruptedException e) {
    //  e.printStackTrace();
    //}
  }*/
}
