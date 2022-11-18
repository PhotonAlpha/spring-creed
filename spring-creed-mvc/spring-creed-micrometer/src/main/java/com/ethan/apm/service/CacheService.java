/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.apm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 11/1/2022 5:35 PM
 */
@Service
public class CacheService {
    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    @Cacheable(value = "STATIC_DATA_CACHE", key = "#root.methodName + '_'+ #val", unless = "#result == null")
    public String getVal(Double val) {
        log.info("call val:{}", val);
        return "cache with val " + val;
    }
    @Cacheable(value = "CUSTOM_CACHE", key = "#root.methodName + '_'+ #val", unless = "#result == null")
    public String getVal2(Double val) {
        log.warn("call val2:{}", val);
        return "cache with val " + val;
    }
}
