package com.creed.controller;

import com.creed.model.Account;
import com.creed.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ExampleController {
  private final AccountService accountService;
  private final CsrfTokenRepository tokenRepository;

  public ExampleController(AccountService accountService, CsrfTokenRepository tokenRepository) {
    this.accountService = accountService;
    this.tokenRepository = tokenRepository;
  }

  @GetMapping("/echo")
    public String demo() {
      return "示例返回";
    }

    @GetMapping("/home")
    public String home() {
      return "我是首页";
    }

    @GetMapping("/admin")
    public String admin() {
      return "我是管理员";
    }

    @GetMapping("/normal")
    public String normal() {
      return "我是普通用户";
    }

    @PostMapping("/user")
    public ResponseEntity<Account> createUser(@RequestBody Account account) {
      Account acc = accountService.addAccount(account);
      return ResponseEntity.ok(acc);
    }
    @GetMapping("/token")
    public ResponseEntity<String> getToken(HttpServletRequest request) {
      String token = Optional.ofNullable(tokenRepository)
          .map(re -> re.loadToken(request))
          .map(CsrfToken::getToken).orElse("");
      return ResponseEntity.ok(token);
    }
}
