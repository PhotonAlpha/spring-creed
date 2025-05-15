package com.ethan.exchange.client;

import com.ethan.SampleValidatorApplication;
import com.ethan.validator.controller.vo.MyAccountDetailsVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/3/25
 */
@SpringBootTest(classes = SampleValidatorApplication.class)
@Slf4j
public class ArtisanClientTest {
    @Resource
    ArtisanClient artisanClient;
    @Resource
    ArtisanClientX artisanClientX;

    @Test
    void name() {
        var detailsVO = new MyAccountDetailsVO();
        detailsVO.setCode("S");
        try {
            var httpHeaders = new HttpHeaders();
            httpHeaders.add("x-api-key", "x-api-key-junit");
            var params = Map.of("pageSize", "11");

            ResponseEntity<List<MyAccountDetailsVO>> myAccountDetails = artisanClient.listUser(detailsVO, httpHeaders, params);
            myAccountDetails.getHeaders().forEach((k, val) -> {
                System.out.println("header key:%s value:%s".formatted(k, val));
            });
            for (var myAccountDetail : myAccountDetails.getBody()) {
                System.out.println(myAccountDetail);
            }
        } catch (Exception e) {
            if (e instanceof HttpClientErrorException ex) {
                ex.getResponseHeaders().forEach((k, val) -> {
                    System.out.println("header from error key:%s value:%s".formatted(k, val));
                });
            }
            log.error("listUser exception:", e);
        }


        /* try {
            var httpHeaders = new HttpHeaders();
            httpHeaders.add("x-api-key", "x-api-key-junit");
            var params = Map.of("pageSize", "11");

            ResponseEntity<List<MyAccountDetailsVO>> myAccountDetails = artisanClientX.listUser(detailsVO, httpHeaders, params);
            myAccountDetails.getHeaders().forEach((k, val) -> {
                System.out.println("header key:%s value:%s".formatted(k, val));
            });
            for (var myAccountDetail : myAccountDetails.getBody()) {
                System.out.println(myAccountDetail);
            }
        } catch (Exception e) {
            if (e instanceof HttpClientErrorException ex) {
                ex.getResponseHeaders().forEach((k, val) -> {
                    System.out.println("header from error key:%s value:%s".formatted(k, val));
                });
            }
            log.error("listUser exception:", e);
        } */
    }
}
