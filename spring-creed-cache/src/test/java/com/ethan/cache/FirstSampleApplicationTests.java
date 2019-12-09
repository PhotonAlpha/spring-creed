package com.ethan.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FirstSampleApplicationTests {
  //@Autowired
  ApplicationContext applicationContext;

  @Test
  public void contextLoads() {
    //stringRedisTemplate.opsForValue().set("name", "chen");
    Assertions.assertEquals("chen", "chen");
  }
}
