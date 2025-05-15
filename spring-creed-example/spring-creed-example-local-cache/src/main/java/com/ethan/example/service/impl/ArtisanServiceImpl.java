package com.ethan.example.service.impl;

import com.ethan.example.controller.vo.ArtisanDetailsVO;
import com.ethan.example.service.ArtisanService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.ethan.example.config.CachePropertiesBean.DEFAULT_CACHE;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 1/4/25
 */
@Service
@CacheConfig(cacheNames = DEFAULT_CACHE)
@Slf4j
public class ArtisanServiceImpl implements ArtisanService {
    public static final AtomicInteger INDEX = new AtomicInteger(1);
    @Override
    // @Cacheable(key = "'EntityPermissionsV2_' + #id +'_'+ #name", sync = true) // 会缓存null
    // @Cacheable(key = "'EntityPermissionsV2_' + #id +'_'+ #name") // 会缓存null
    @Cacheable(key = "'EntityPermissionsV2_' + #id +'_'+ #name", unless = "#result == null ") // 不会缓存null
    public ArtisanDetailsVO findByIdOrName(String id, String name) {
        log.info("hint findByIdOrName id:{} name:{}", id, name);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (INDEX.incrementAndGet() < 10) {
            return null;
        }
        // var random = new Random();
        // if(random.nextInt(9) % 2 == 1)
            return new ArtisanDetailsVO(id, RandomStringUtils.randomAlphanumeric(10), name, "pwd", RandomStringUtils.randomAlphanumeric(5) + "@gmail.com", RandomStringUtils.randomNumeric(1), RandomStringUtils.randomNumeric(11));
        // return null;
    }
}
