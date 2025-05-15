package com.ethan.example.service;

import com.ethan.example.controller.vo.ArtisanDetailsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.ethan.example.config.logging.ThreadPoolConfig.TASK_EXECUTOR;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 2/4/25
 */
@Service
@Slf4j
public class ThreadDemoService {
    @Async
    public CompletableFuture<ArtisanDetailsVO> getUsers(String mainT) {
        ArtisanDetailsVO res = new ArtisanDetailsVO(RandomStringUtils.randomNumeric(3), RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), "pwd", RandomStringUtils.randomAlphanumeric(5) + "@gmail.com", RandomStringUtils.randomNumeric(1), RandomStringUtils.randomNumeric(11));
        log.info("mainT:{} getUsers:{}", mainT, res);
        try {
            TimeUnit.MICROSECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // throw new RuntimeException("exception testing");
        return CompletableFuture.completedFuture(res);
    }
    @Async(TASK_EXECUTOR)
    public CompletableFuture<ArtisanDetailsVO> getUsersX(String mainT) {
        ArtisanDetailsVO res = new ArtisanDetailsVO(RandomStringUtils.randomNumeric(3), RandomStringUtils.randomAlphanumeric(10), RandomStringUtils.randomAlphanumeric(10), "pwd", RandomStringUtils.randomAlphanumeric(5) + "@gmail.com", RandomStringUtils.randomNumeric(1), RandomStringUtils.randomNumeric(11));
        log.info("mainT:{} getUsersX:{}", mainT, res);
        try {
            TimeUnit.MICROSECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(res);
    }
}
