package com.ethan.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author EthanCao
 * @description Default (Template) Project
 * @date 17/4/25
 */
@EnableConfigServer
@SpringBootApplication
public class SpringCloudConfigServerApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigServerApp.class, args);
    }
}