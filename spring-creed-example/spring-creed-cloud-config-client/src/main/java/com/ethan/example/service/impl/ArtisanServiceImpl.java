package com.ethan.example.service.impl;

import com.ethan.example.controller.vo.ArtisanDetailsVO;
import com.ethan.example.exception.BusinessException;
import com.ethan.example.service.ArtisanService;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 1/4/25
 */
@Service
@Slf4j
public class ArtisanServiceImpl implements ArtisanService {
    public static final AtomicInteger INDEX = new AtomicInteger(4);
    private static final List<ArtisanDetailsVO> PERSISTENCE_LIST = new ArrayList<>(
            List.of(
                    new ArtisanDetailsVO(
                            "1",
                            "code_0a3174ca507a",
                            "name_89ca454c54d9",
                            "password_8afa284ffe49",
                            "email_0f7547ebd0a8",
                            "sex_38d319192c1b",
                            "phone_35e91d3e7f8a"
                    ),
                    new ArtisanDetailsVO(
                            "2",
                            "code_9262308f11cf",
                            "name_9e1e298929c1",
                            "password_23d9fe32cb1b",
                            "email_8c7faa25f378",
                            "sex_f7c88e92f2a0",
                            "phone_b7cf555706aa"
                    ), new ArtisanDetailsVO(
                            "3",
                            "code_668d927d9f62",
                            "name_d85048f5676d",
                            "password_c1187360e64d",
                            "email_3c4d9429642f",
                            "sex_93da657b7cb9",
                            "phone_e7f322591c03"
                    )
            ));
    private static final String BACKEND_A = "backendA";
    private static final AtomicInteger COUNT_INDEX = new AtomicInteger(0);
    public static final AtomicInteger RANDOM_INDEX = new AtomicInteger(0);

    @Override
    @Retry(name = BACKEND_A, fallbackMethod = "fallback")
    public ArtisanDetailsVO findByIdOrName(String id, String name) {
        log.info("findByIdOrName id:{} name:{}", id, name);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int currentIndex = COUNT_INDEX.incrementAndGet();

        log.info("currentIndex:{}", currentIndex);
        if (currentIndex < RANDOM_INDEX.get()) {
            throw new BusinessException("Retry Testing");
        }
        try {
            return PERSISTENCE_LIST.stream().filter(r -> StringUtils.equals(id, r.getId()) || StringUtils.equals(name, r.getName()))
                    .findFirst().orElse(null);
        } finally {
            COUNT_INDEX.setRelease(0);
        }
    }

    private ArtisanDetailsVO fallback(String id, String name, BusinessException ex) {
        log.info("Recovered BusinessException: {} id:{} name:{}", ex.toString(), id, name);
        COUNT_INDEX.setRelease(0);
        return new ArtisanDetailsVO(
                "Business fallback:" + id,
                null,
                "Business fallback:" + name,
                null,
                null,
                null,
                null
        );
    }

    private ArtisanDetailsVO fallback(Exception ex) {
        log.info("Recovered Exception: {}", ex.toString());
        COUNT_INDEX.setRelease(0);
        return new ArtisanDetailsVO(
                "NA",
                "fallback",
                null,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public List<ArtisanDetailsVO> findAll() {
        log.info("findAll");
        return PERSISTENCE_LIST;
    }

    @Resource(name = "remoteClusterRestTemplate")
    RestTemplate restTemplate;
    @Resource
    MeterRegistry meterRegistry;
    @Override
    public List<ArtisanDetailsVO> findAllRemote() {
        ParameterizedTypeReference<List<ArtisanDetailsVO>> typeResp = new ParameterizedTypeReference<List<ArtisanDetailsVO>>() {

        };
        HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        ResponseEntity<List<ArtisanDetailsVO>> response = restTemplate.exchange("http://remote-cluster/buziVa/artisan/list", HttpMethod.GET, httpEntity, typeResp, Map.of());
        return response.getBody();
    }
}
