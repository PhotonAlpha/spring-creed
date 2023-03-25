package com.ethan.controller;

import com.ethan.common.R;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api2/v1/system/auth")
@Slf4j
@Tag(name = "BasicAuthController")
public class BasicAuthController {

    @Schema(name = "login", description = "登录", implementation = String.class)
    @GetMapping("/login")
    public R<String> login() {
        return R.success("success");
    }
}
