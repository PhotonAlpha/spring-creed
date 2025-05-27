package com.ethan.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.retry.annotation.EnableRetry;

/**
 * @author EthanCao
 * @description Default (Template) Project
 * @date 29/4/25
 */
@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
@EnableRetry
public class SpringCloudConfigClientApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigClientApp.class, args);
    }
}