package com.ethan.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
public class FirstSampleApplicationTests {
  @Autowired
  private StringRedisTemplate stringRedisTemplate;

  @Test
  public void contextLoads() {
    // Jackson2JsonRedisSerializer
    stringRedisTemplate.opsForValue().set("name", "ethan");
    Assertions.assertEquals("ethan", stringRedisTemplate.opsForValue().get("name"));
  }
}
