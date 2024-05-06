package com.ethan.scheduler;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 11/4/24
 */
// @Configuration
// @EnableScheduling
@Slf4j
public class SchedulerConfig {
    @Resource
    private RestTemplate restTemplate;

    @Scheduled(cron = "*/10 * * * * *")
    public void testRestTemplate() {
        log.info("test==:{}",Thread.currentThread().getName());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:4200/gebng/showAll", String.class);
        String msgResp = responseEntity.getBody();
        log.info("msgResp:{}", StringUtils.abbreviate(msgResp, 30));
    }
    @Scheduled(cron = "*/11 * * * * *")
    public void testRestTemplate2() {
        log.info("testRestTemplate2==:{}",Thread.currentThread().getName());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:4200/gebng/showAll", String.class);
        String msgResp = responseEntity.getBody();
        log.info("msgResp:{}", StringUtils.abbreviate(msgResp, 30));
    }
    @Scheduled(cron = "*/9 * * * * *")
    public void testRestTemplate3() {
        log.info("testRestTemplate3==:{}",Thread.currentThread().getName());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:4200/gebng/showAll", String.class);
        String msgResp = responseEntity.getBody();
        log.info("msgResp:{}", StringUtils.abbreviate(msgResp, 30));
    }
    @Scheduled(cron = "*/10 * * * * *")
    public void testRestTemplate4() {
        log.info("testRestTemplate4==:{}",Thread.currentThread().getName());
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://localhost:4200/gebng/showAll", String.class);
        String msgResp = responseEntity.getBody();
        log.info("msgResp:{}", StringUtils.abbreviate(msgResp, 30));
    }
}
