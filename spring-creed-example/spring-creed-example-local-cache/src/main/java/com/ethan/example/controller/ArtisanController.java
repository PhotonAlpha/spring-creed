package com.ethan.example.controller;

import com.ethan.example.controller.vo.ArtisanDetailsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@RestController
@RequestMapping("/buziVa/artisan")
@Slf4j
public class ArtisanController {

    // POST 方法
    @PostMapping
    public ArtisanDetailsVO createUser(@RequestBody ArtisanDetailsVO user) {
        log.info("createUser:{}", user);
        return user;
    }


}
