/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.controller;

import com.ethan.dto.LoginDto;
import com.ethan.vo.AccountInfoVo;
import com.ethan.vo.R;
import com.ethan.vo.TokenVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/20/2022 10:32 AM
 */
@RestController
@RequestMapping("/api/v1")
public class LoginController {
    public static final Map<String, AccountInfoVo> INFO = new HashMap<>();
    static {
        INFO.put("admin-token", new AccountInfoVo(Arrays.asList("admin"),
                "I am a super administrator",
                "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
                "Super Admin"));
        INFO.put("editor-token", new AccountInfoVo(Arrays.asList("editor"),
                "I am an editor",
                "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif",
                "Normal Editor"));
    }

    @PostMapping("/user/login")
    public R login(@RequestBody LoginDto dto) {
        if (StringUtils.equals("admin", dto.getUsername())
                && StringUtils.equals("ethan", dto.getPassword())) {
            return R.success(new TokenVo("admin-token"));
        } else {
            return R.error(60204, "Account and password are incorrect.");
        }
    }

    @GetMapping("/user/info")
    public R getInfo(@RequestParam("token") String token) {
        AccountInfoVo accountInfo = INFO.getOrDefault(token, null);
        if (accountInfo == null) {
            return R.error(50008, "Login failed, unable to get user details.");
        } else {
            return R.success(accountInfo);
        }
    }
}
