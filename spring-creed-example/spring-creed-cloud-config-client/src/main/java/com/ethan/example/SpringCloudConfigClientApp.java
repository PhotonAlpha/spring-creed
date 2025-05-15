package com.ethan.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author EthanCao
 * @description Default (Template) Project
 * @date 29/4/25
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudConfigClientApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigClientApp.class, args);
    }
}