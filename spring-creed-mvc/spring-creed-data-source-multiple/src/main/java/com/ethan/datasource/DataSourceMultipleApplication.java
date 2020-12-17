package com.ethan.datasource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true) // http://www.voidcn.com/article/p-zddcuyii-bpt.html
 @EnableAspectJAutoProxy(exposeProxy = true) // http://www.voidcn.com/article/p-zddcuyii-bpt.html
public class DataSourceMultipleApplication {
  public static void main(String[] args) {
    SpringApplication.run(DataSourceMultipleApplication.class, args);
  }
}
