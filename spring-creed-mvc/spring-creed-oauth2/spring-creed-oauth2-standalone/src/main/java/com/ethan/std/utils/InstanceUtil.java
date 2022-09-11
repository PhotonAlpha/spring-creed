package com.ethan.std.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 4:44 PM
 */
public class InstanceUtil {
    public static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Jackson2JsonRedisSerializer<OAuth2Authentication> oAuth2AuthenticationSerializer = new Jackson2JsonRedisSerializer<>(OAuth2Authentication.class);
    }
    private InstanceUtil() {
    }
}
