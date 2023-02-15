package com.ethan.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.ethan")
@EnableJpaRepositories(basePackages = {
        "com.ethan.security.oauth2.repository",
        "com.ethan.security.websecurity.repository",
        "com.ethan.web.logger.core",
        "com.ethan.system.dal.repository"
})
@EntityScan(basePackages = {
        "com.ethan.security.oauth2.entity",
        "com.ethan.security.websecurity.entity",
        "com.ethan.web.logger.core.entity",
        "com.ethan.system.dal.entity"
})
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
