package com.ethan.creedmall.ware;

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
@MapperScan("com.ethan.creedmall.ware.dao")
public class WareApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareApplication.class, args);
    }
}
