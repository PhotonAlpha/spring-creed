/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.redis;

import org.springframework.boot.SpringApplication;

// @SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
// @EnableCaching
public class RedisApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisApplication.class, args);
    }
}
