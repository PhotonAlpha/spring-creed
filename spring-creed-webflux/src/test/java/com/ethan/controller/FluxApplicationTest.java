/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.controller;

import com.ethan.FluxApplication;
import com.ethan.service.AsyncServiceImpl;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.task.TaskExecutor;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/30/2022 4:07 PM
 */
@SpringBootTest(classes = FluxApplication.class)
public class FluxApplicationTest {
    private static final Logger log = LoggerFactory.getLogger(FluxApplicationTest.class);
    @Autowired
    @Qualifier("callerExecutor")
    private TaskExecutor taskExecutor;

    @Autowired
    private AsyncServiceImpl asyncService;

    @Value("${semaphore.max:10}")
    private Integer maxSemaphore;

    public Semaphore SEMAPHORE; // 模拟3个车位

    @PostConstruct
    public void init() {
        SEMAPHORE = new Semaphore(maxSemaphore);
    }

    @Test
    void testLock() {
        List<CompletableFuture<String>> list = new ArrayList<>();
        log.info("------------------start @ batch");

        for (int i = 0; i < 500; i++) {
            CompletableFuture<String> completableFuture = asyncService.callRemoteApi(i + 1);
            list.add(completableFuture);
            // int finalI = i;
            // taskExecutor.execute(() -> {
            //     try {
            //         log.info("=====thread start:{} index:{}", Thread.currentThread().getName(), finalI);
            //         SEMAPHORE.tryAcquire(20, TimeUnit.SECONDS);
            //         CompletableFuture<String> completableFuture = asyncService.callRemoteApi();
            //         completableFuture.get();
            //         TimeUnit.SECONDS.sleep(30);
            //         log.info("=====thread end:{} index:{}", Thread.currentThread().getName(), finalI);
            //     } catch (Exception e) {
            //         log.error("asyncService", e);
            //     } finally {
            //         SEMAPHORE.release();
            //     }
            // });
        }
        log.info("------------------send @ batch");
        list.forEach(CompletableFuture::join);
        log.info("------------------end @ batch");
    }
}
