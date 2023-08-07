package com.ethan;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = "com.ethan")
public class TestingServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestingServerApplication.class, args);
    }
}
