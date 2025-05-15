package com.ethan.system.controller;

import com.ethan.common.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api2/v1/system/auth")
@Slf4j
public class BasicAuthController {
    @GetMapping("/login")
    public R<String> login() {
        return R.success("success");
    }
}
