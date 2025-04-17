package com.ethan.validator.controller;

import com.ethan.validator.controller.vo.MyAccountDetailsVO;
import com.ethan.validator.context.JsonSchemaValidated;
import com.ethan.validator.repository.ArtisanDao;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
    public MyAccountDetailsVO updateUser(@JsonSchemaValidated(schemaUri = "my-account-update") @RequestBody MyAccountDetailsVO artisan) {
        MyAccountDetailsVO editUser = artisanDao.save(artisan);
        log.info("update artisan is {}", editUser);
        return editUser;
    }
}
