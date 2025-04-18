/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@Slf4j
public class LoginController {
    // @Resource
    private CsrfTokenRepository tokenRepository;
    @GetMapping("/oauth/index")
    public String login(HttpServletRequest request, Model model, @RequestParam(value = "error", required = false) String error) {
        CsrfToken csrfToken = new DefaultCsrfToken("X", "A", UUID.randomUUID().toString());//tokenRepository.loadToken(request);
        log.info("getToken.....{}", csrfToken.getToken());
        if (error != null) {
            model.addAttribute("error", "用户名或密码错误");
        }
        model.addAttribute(csrfToken.getParameterName(), csrfToken.getToken());
        return "base-login";
    }

    @GetMapping("/oauth/active")
    public String active(HttpServletRequest request, Model model) {
        return "active";
    }

    /**
     @Controller
    public class IndexController {
        // 仅匹配前端 处理刷新问题
        // @RequestMapping({"/{path:[^\\\\.]*}"})
        @RequestMapping({"/index", "/system/*"})
        public String index() {
            return "forward:/";
        }
    } */
}
