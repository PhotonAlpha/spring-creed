package com.ethan.system.dal.redis.common;

import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.ethan.system.dal.redis.core.RedisKeyConstants.CAPTCHA_CODE;

/**
 * 验证码的 Redis DAO
 *
 * 
 */
@Repository
public class CaptchaRedisDAO {
    public static final Map<String, String> stringRedisTemplate = new HashMap<>();

    /* @Resource
    private StringRedisTemplate stringRedisTemplate;
    */
    public String get(String uuid) {
        String redisKey = formatKey(uuid);
        // return stringRedisTemplate.opsForValue().get(redisKey);
        return stringRedisTemplate.get(redisKey);
    }

    public void set(String uuid, String code, Duration timeout) {
        String redisKey = formatKey(uuid);
        // stringRedisTemplate.opsForValue().set(redisKey, code, timeout);
        stringRedisTemplate.put(redisKey, code);
    }

    public void delete(String uuid) {
        String redisKey = formatKey(uuid);
        // stringRedisTemplate.delete(redisKey);
        stringRedisTemplate.remove(redisKey);
    }

    private static String formatKey(String uuid) {
        return String.format(CAPTCHA_CODE.getKeyTemplate(), uuid);
    }

}
