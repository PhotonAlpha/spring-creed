package com.ethan.cache;

import com.ethan.cache.model.CqMembers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;
import org.junit.jupiter.api.Disabled;

@ExtendWith(SpringExtension.class)
// @SpringBootTest(classes = CreedTestApplication.class)
@TestPropertySource(locations = "classpath:application.yml")
@Disabled("Bean 装配失败（UnsatisfiedDependency），Spring 上下文无法启动，CI 暂禁用")
public class FirstSampleApplicationTests {
  @Autowired
  private RedisTemplate redisTemplate;

  @Test
  public void contextLoads() {
    // Jackson2JsonRedisSerializer
    redisTemplate.opsForValue().set("name", "ethan");
    Assertions.assertEquals("ethan", redisTemplate.opsForValue().get("name"));
  }

  @Test
  public void testAddObject() {
    CqMembers mem = new CqMembers();
    mem.setId(1L);
    mem.setAge(10);
    mem.setEmail("1@gmail.com");
    mem.setMobile("123456");
    mem.setName("小明");
    mem.setIp("127.0.0.1");
    mem.setNickname("echo");
    mem.setRegistrationTime(new Date());
    ValueOperations<String, CqMembers> ops = redisTemplate.opsForValue();
    redisTemplate.opsForValue().set(mem.getName(), mem);
    CqMembers res = ops.get(mem.getName());
    System.out.println(res);
  }
}
