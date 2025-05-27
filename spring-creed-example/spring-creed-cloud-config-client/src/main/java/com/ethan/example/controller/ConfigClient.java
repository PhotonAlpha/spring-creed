package com.ethan.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 29/4/25
 */
@Slf4j
@RestController
public class ConfigClient {
    @Value("${URL_A:}")
    private String urlA;
    @Resource
    ConfigurableEnvironment environment;

    @GetMapping("/list")
    public Map<String, Object> getAllProperties() {
        Map<String, Object> map = new HashMap();
        log.info("urlA:{}", urlA);
        for (var propertySource : environment.getPropertySources()) {
            if (propertySource instanceof OriginTrackedMapPropertySource mps) {
                map.putAll(mps.getSource());
            }
        }
        return map.entrySet().stream().filter(entry -> entry.getValue() instanceof String)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
