package com.ethan.gradation.util;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * redis扩展工具
 *
 */
public abstract class RedisHelper {
    /**
     * scan 实现
     *
     * @param redisTemplate redisTemplate
     * @param pattern       表达式
     */
    public static Set<String> scan(RedisTemplate<String, Object> redisTemplate, String pattern) {
        return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            // Set<String> keysTmp = new HashSet<>();
            // try (Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder()
            //         .match(pattern)
            //         .count(10000).build())) {
            //
            //     while (cursor.hasNext()) {
            //         keysTmp.add(new String(cursor.next(), StandardCharsets.UTF_8));
            //     }
            // } catch (Exception e) {
            //     throw new RuntimeException(e);
            // }
            // return keysTmp;
            return null;
        });
    }
}

