/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.apm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 10/31/2022 10:42 AM
 */
@RestController
@RequestMapping("/api/v1")
public class FileResourcesTestingController {
    private static final Logger log = LoggerFactory.getLogger(FileResourcesTestingController.class);

    // @Value("${batch.filepath:file:/Users/caoqiang/Downloads/*.JSON}")
    // private String filePath;

    @GetMapping("/list")
    public ResponseEntity<List<String>> createLogs(@RequestParam("path") String filePath) {
        List<String> list = new ArrayList<>();
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = patternResolver.getResources(filePath);
            for (Resource resource : resources) {
                list.add(resource.getFile().getAbsolutePath());
                list.add(resource.getFile().getPath());
            }
        } catch (Exception e) {
            log.error("runtime exception", e);
        }
        return ResponseEntity.ok(list);
    }
}
