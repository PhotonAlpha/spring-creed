package com.creed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IndexController {
  private final CsrfTokenRepository tokenRepository;

  public IndexController(CsrfTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  @GetMapping("/oauth/index")
  public String login(HttpServletRequest request, Model model, @RequestParam(value = "error", required = false) String error) {
    CsrfToken csrfToken = tokenRepository.loadToken(request);
    System.out.println("index....." + csrfToken.getToken());
    if (error != null) {
      model.addAttribute("error", "用户名或密码错误");
    }
    model.addAttribute(csrfToken.getParameterName(), csrfToken.getToken());
    ///authentication/base
    return "base-login";
  }
}
