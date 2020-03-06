package com.creed.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class OAuthGrantController {
  @PostMapping("/index")
  public String login() {
    return "index";
  }
  @GetMapping("/info")
  public Object info(Principal principal) {
    return principal;
  }
}
