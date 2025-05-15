package com.ethan.validator.controller;

import com.ethan.validator.context.CreedIdValidator;
import com.ethan.validator.controller.vo.MyAccountDetailsVO;
import com.ethan.validator.context.JsonSchemaValidated;
import com.ethan.validator.controller.vo.PageParam;
import com.ethan.validator.repository.ArtisanDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@RestController
@RequestMapping("/buziVa/artisan")
@Slf4j
public class ArtisanController {
    @Autowired
    private ArtisanDao artisanDao;


    // POST 方法
    @PostMapping
    public MyAccountDetailsVO createUser(@JsonSchemaValidated(schemaUri = "my-account-create") @RequestBody MyAccountDetailsVO user) {
        MyAccountDetailsVO savedUser = artisanDao.save(user);
        log.info("save user id is {}", savedUser.getId());
        return savedUser;
    }

    // PUT
    @SneakyThrows
    @PutMapping
    public MyAccountDetailsVO updateUser(@JsonSchemaValidated(schemaUri = "my-account-update", constraints = CreedIdValidator.class) @RequestBody MyAccountDetailsVO artisan) {
        MyAccountDetailsVO editUser = artisanDao.save(artisan);
        log.info("update artisan is {}", editUser);
        return editUser;
    }


    @PostMapping("list")
    public List<MyAccountDetailsVO> getUser(@RequestBody MyAccountDetailsVO user, @RequestHeader HttpHeaders httpHeaders, @RequestParam Map<String, String> pageParam, HttpServletRequest httpServletRequest) {
        var cookies = httpServletRequest.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (var cookie : cookies) {
                System.out.println("receive cookie key:%s value:%s".formatted(cookie.getName(), cookie.getValue()));
            }
        }
        httpHeaders.forEach((k, val) -> {
            System.out.println("receive header key:%s value:%s".formatted(k, val));
        });

        pageParam.forEach((k, val) -> {
            System.out.println("receive param key:%s value:%s".formatted(k, val));
        });
        var random = new Random().nextInt(10);
        // if (random % 2 == 0) {
        //     throw new RuntimeException("invalid request");
        // }
        List<MyAccountDetailsVO> savedUser = artisanDao.list(user);
        return savedUser;
    }

    /**
     * disable chunk
     * @param user
     * @param httpHeaders
     * @param pageParam
     * @param httpServletRequest
     * @return
     */
   /*  @PostMapping("list")
    public ResponseEntity<List<MyAccountDetailsVO>> getUser(@RequestBody MyAccountDetailsVO user, @RequestHeader HttpHeaders httpHeaders, @RequestParam Map<String, String> pageParam, HttpServletRequest httpServletRequest) throws JsonProcessingException {
        var cookies = httpServletRequest.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            for (var cookie : cookies) {
                System.out.println("receive cookie key:%s value:%s".formatted(cookie.getName(), cookie.getValue()));
            }
        }
        httpHeaders.forEach((k, val) -> {
            System.out.println("receive header key:%s value:%s".formatted(k, val));
        });

        pageParam.forEach((k, val) -> {
            System.out.println("receive param key:%s value:%s".formatted(k, val));
        });
        var random = new Random().nextInt(10);
        // if (random % 2 == 0) {
        //     throw new RuntimeException("invalid request");
        // }
        HttpHeaders headers = new HttpHeaders();

        List<MyAccountDetailsVO> savedUser = artisanDao.list(user);
        headers.set(HttpHeaders.CONTENT_LENGTH, StringUtils.getBytes(new ObjectMapper().writeValueAsString(savedUser), StandardCharsets.UTF_8).length + "");
        return  new ResponseEntity<>(savedUser, headers, HttpStatus.OK);
    } */
}
