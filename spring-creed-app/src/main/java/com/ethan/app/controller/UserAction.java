/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/08
 */
package com.ethan.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class UserAction {
  @GetMapping(value = "me")
  public Principal me(Principal principal) {
    System.out.println("调用me接口获取用户信息：" + principal);
    return principal;
  }
}
