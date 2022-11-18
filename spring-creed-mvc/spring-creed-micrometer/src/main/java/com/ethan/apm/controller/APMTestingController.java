/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.apm.controller;

import com.ethan.apm.service.CacheService;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 10/31/2022 10:42 AM
 */
@RestController
@RequestMapping("/api/v1")
public class APMTestingController {
    private static final Logger log = LoggerFactory.getLogger(APMTestingController.class);

    Counter visitCounter;

    @Autowired
    private CacheService cacheService;


    public APMTestingController(MeterRegistry registry) {
        this.visitCounter = Counter.builder("visit_counter")
                .description("Number of visits to the site")
                .register(registry);
    }

    @Autowired
    private CacheManager manager;

    @GetMapping("/testing")
    public ResponseEntity<String> createLogs() {

        HashMap<String, Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("a", "b");

        log.info("just check");
        String preRes = cacheService.getVal(visitCounter.count());
        String preRes2 = cacheService.getVal2(visitCounter.count());
        visitCounter.increment();
        String res = cacheService.getVal(visitCounter.count());
        String res2 = cacheService.getVal2(visitCounter.count());
        byte[] bytes = new byte[20 * 1024 * 1024];


        CacheStats stats = ((CaffeineCache) manager.getCache("STATIC_DATA_CACHE")).getNativeCache().stats();
        CacheStats stats2 = ((CaffeineCache) manager.getCache("CUSTOM_CACHE")).getNativeCache().stats();
        log.info("preRes:{} res:{}", preRes, res);
        log.info("preRes2:{} res2:{}", preRes2, res2);
        log.info("stats:{}", stats);
        log.info("stats2:{}", stats2);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok("All ok. pre__" + preRes + " cur__" + res);
    }
    @PostMapping("/alert")
    public void alertReceive(@RequestBody String alert) {
        log.info("alertReceive:{}", alert);
    }
}
