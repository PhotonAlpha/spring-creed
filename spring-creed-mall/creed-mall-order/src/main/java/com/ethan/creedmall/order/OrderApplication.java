package com.ethan.creedmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @className: ProductApplication
 * @author: Ethan
 * @date: 15/3/2022
 **/
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.ethan.creedmall.order.dao")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
