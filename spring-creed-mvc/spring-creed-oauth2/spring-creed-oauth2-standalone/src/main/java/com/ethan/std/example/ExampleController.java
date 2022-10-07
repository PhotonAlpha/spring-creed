package com.ethan.std.example;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/1/2022 2:37 PM
 */
@RestController
@RequestMapping("/api/example")
public class ExampleController {
    @RequestMapping("/hello")
    public String hello() {
        return "world";
    }

}
