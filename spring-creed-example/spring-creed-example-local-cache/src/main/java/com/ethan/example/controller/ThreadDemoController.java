package com.ethan.example.controller;

import com.ethan.example.controller.vo.ArtisanDetailsVO;
import com.ethan.example.service.ThreadDemoService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 3/4/25
 */
@RestController
@RequestMapping("api")
@Slf4j
public class ThreadDemoController {
    @Resource
    ThreadDemoService threadDemoService;
    @Resource
    ThreadPoolTaskExecutor taskExecutor;
    @Resource
    RestTemplate restTemplate;

    @GetMapping("demo")
    public ArtisanDetailsVO myThreadCheck() {
        try {
            var mainT = Thread.currentThread().getName();
            log.info("@@myThreadCheck:{}@@", mainT);
            CompletableFuture.runAsync(() -> log.info("abc"));
            var completableFuture1 = threadDemoService.getUsers(mainT);
            var completableFuture2 = threadDemoService.getUsersX(mainT);
            CompletableFuture.allOf(completableFuture1, completableFuture2).join();
            var responseEntity = restTemplate.getForEntity("http://localhost:8080/api/show-trace", String.class);
            log.info("responseEntity:{}", responseEntity.getBody());
            log.info("@@myThreadCheck ending:{}@@", mainT);
            return completableFuture1.get();
        } catch (Exception e) {
            log.error("CompletableFuture:", e);
        }
        return null;
    }

    @GetMapping("show-trace")
    public String showTrace(@RequestHeader HttpHeaders httpHeaders) {
        Map<String, String> singleValueMap = httpHeaders.toSingleValueMap();
        var mainT = Thread.currentThread().getName();
        log.info("@@showTrace ending:{}@@ singleValueMap:{}", mainT, singleValueMap);
        return "greetings";
    }
}
