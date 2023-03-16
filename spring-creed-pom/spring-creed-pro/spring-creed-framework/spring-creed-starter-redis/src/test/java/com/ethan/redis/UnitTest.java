/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.redis;

import com.ethan.redis.service.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnitTest {

    public static void main(String[] args) {
        String s = """
                        [{"@class":"com.ethan.redis.service.User","id":"001","name":"ming","amount":["java.math.BigDecimal",10],"age":18},{"@class":"com.ethan.redis.service.User","id":"002","name":"hang","amount":["java.math.BigDecimal",1],"age":21},{"@class":"com.ethan.redis.service.User","id":"003","name":"on","amount":["java.math.BigDecimal",0],"age":19},{"@class":"com.ethan.redis.service.User","id":"004","name":"crisp","amount":["java.math.BigDecimal",10],"age":20}]
                """;

        Jackson2JsonRedisSerializer serializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);
        // RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // redisTemplate.setConnectionFactory(redisConnectionFactory);
        // redisTemplate.setKeySerializer(new StringRedisSerializer());
        // redisTemplate.setValueSerializer(serializer);
        // redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // redisTemplate.setHashValueSerializer(serializer);
        // redisTemplate.afterPropertiesSet();
        // return redisTemplate;
        List<User> list = new ArrayList<>();
        list.add(new User("001", "ming", BigDecimal.TEN, 18));

        byte[] serialize = serializer.serialize(list.stream().collect(Collectors.toList()));
        String s1 = new String(serialize);
        System.out.println(s1);
        Object deserialize = serializer.deserialize(s1.getBytes(StandardCharsets.UTF_8));
        System.out.println(deserialize);
    }
}
