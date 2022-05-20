package com.ethan.creedmall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @className: ThirdPartyApplication
 * @author: Ethan
 * @date: 24/3/2022
 **/
@EnableDiscoveryClient
@SpringBootApplication
public class ThirdPartyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThirdPartyApplication.class, args);
    }
}
