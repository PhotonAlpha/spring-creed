package com.ethan.creedmall.coupon;

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
@MapperScan("com.ethan.creedmall.coupon.dao")
public class CouponApplication {
    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }
}
