package com.ethan.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author EthanCao
 * @description Default (Template) Project
 * @date 7/17/24
 */
@SpringBootApplication
@EnableJdbcRepositories(basePackages = "com.ethan.example.jdbc.repository")
@EnableJpaRepositories(basePackages = "com.ethan.example.jpa.repository")
public class JpaExampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(JpaExampleApplication.class, args);
    }
}