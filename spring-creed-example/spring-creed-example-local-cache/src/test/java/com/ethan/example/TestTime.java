package com.ethan.example;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 2/4/25
 */
@Slf4j
public class TestTime {
    private static final double NANOSEC_UNIT = 1000000000.0;
    @Test
    void name() {

        long startTime = System.nanoTime();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        long endTime = System.nanoTime();
        double elapsedTime = (endTime - startTime) / NANOSEC_UNIT;
        log.info("Time taken by receive - {} ", elapsedTime);
    }

    @Test
    void restTemplateTest() {

        var restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://127.0.0.1:8080/api/demo", String.class);
            String body = responseEntity.getBody();
            System.out.println(body);
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }
}
