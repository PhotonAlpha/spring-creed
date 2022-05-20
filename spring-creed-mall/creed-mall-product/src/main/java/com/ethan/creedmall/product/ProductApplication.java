package com.ethan.creedmall.product;

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
@SpringBootApplication(scanBasePackages = "com.ethan.creedmall")
@MapperScan("com.ethan.creedmall.product.dao")
public class ProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductApplication.class, args);
    }
}
