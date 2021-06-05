package com.creed.security.demo;

import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

//@Controller
public class LoginController {

    @GetMapping("/authentication/login")
    public String authenticationLogin() throws IOException {
        return "login";
    }
}
