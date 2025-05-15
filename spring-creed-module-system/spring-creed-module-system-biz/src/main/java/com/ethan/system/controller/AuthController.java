package com.ethan.system.controller;

import com.ethan.common.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

// @RestController
// @RequestMapping("api/v1/system/auth")
@Slf4j
public class AuthController {
    @GetMapping("/login")
    public R<String> login() {
        return R.success("success");
    }
}
