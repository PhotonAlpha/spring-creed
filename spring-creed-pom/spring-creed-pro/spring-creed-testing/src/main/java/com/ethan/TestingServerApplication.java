package com.ethan;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ethan")
public class TestingServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestingServerApplication.class, args);
    }
}
