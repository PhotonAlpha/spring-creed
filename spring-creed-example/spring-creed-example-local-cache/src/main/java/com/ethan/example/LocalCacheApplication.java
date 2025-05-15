package com.ethan.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

/**
 * @author EthanCao
 * @description Default (Template) Project
 * @date 1/4/25
 */
@SpringBootApplication
@EnableRetry
public class LocalCacheApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocalCacheApplication.class, args);
    }
}