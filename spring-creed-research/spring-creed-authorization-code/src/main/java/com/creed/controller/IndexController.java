package com.creed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@Controller
public class IndexController {

  @GetMapping("/oauth/index")
  public String login(Model model, @RequestParam(value = "error", required = false) String error) {
    System.out.println("index.....");
    if (error != null) {
      model.addAttribute("error", "用户名或密码错误");
    }
    ///authentication/base
    return "base-login";
  }
}
