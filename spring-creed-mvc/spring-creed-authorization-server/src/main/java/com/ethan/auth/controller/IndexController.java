package com.ethan.auth.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class IndexController {
  private final CsrfTokenRepository tokenRepository;

  public IndexController(CsrfTokenRepository tokenRepository) {
    this.tokenRepository = tokenRepository;
  }

  @GetMapping("/oauth/index")
  public String login(HttpServletRequest request, Model model, @RequestParam(value = "error", required = false) String error) {
    CsrfToken csrfToken = tokenRepository.loadToken(request);
    log.info("getToken.....{}", csrfToken.getToken());
    if (error != null) {
      model.addAttribute("error", "用户名或密码错误");
    }
    model.addAttribute(csrfToken.getParameterName(), csrfToken.getToken());
    return "base-login";
  }
}
