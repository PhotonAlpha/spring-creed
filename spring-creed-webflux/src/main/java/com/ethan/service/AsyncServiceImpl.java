/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.service;

import com.ethan.vo.UserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/30/2022 3:23 PM
 */
@Service
public class AsyncServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(AsyncServiceImpl.class);
    @Resource
    private RestTemplate restTemplate;

    @Value("${semaphore.max:10}")
    private Integer maxSemaphore;

    public Semaphore SEMAPHORE; // 模拟3个车位

    @PostConstruct
    public void init() {
        SEMAPHORE = new Semaphore(maxSemaphore);
    }

    @Async("taskExecutor")
    public CompletableFuture<String> callRemoteApi(Integer index) {
        String body = null;
        log.info("{}====start{}", index, Thread.currentThread().getName());
        StopWatch watch = new StopWatch();
        watch.start();
        try {
            SEMAPHORE.acquire();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:8088/api/v1/conn", String.class);
            body = responseEntity.getBody();
            TimeUnit.SECONDS.sleep(3);
        } catch (Exception e) {
            log.error("callRemoteApi occur ", e.getMessage());
        } finally {
            SEMAPHORE.release();
            watch.stop();
        }
        log.info("{}:{}:get response:{} cost:::{}", index, Thread.currentThread().getName(),  body, watch.getTotalTimeMillis());
        return CompletableFuture.completedFuture(body);
    }
}
