package com.creed.security.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @PostMapping(value = "authentication/form", produces = {"application/json", "application/x-www-form-urlencoded"}, consumes = "application/x-www-form-urlencoded")
  public String auth(@RequestParam Map<String, String> body, HttpServletRequest request, HttpSession session) {
    String browser = body.get("browser");
    //取出session中的browser
    Object sessionBrowser = session.getAttribute("browser");
    if (sessionBrowser == null) {
      System.out.println("不存在session，设置browser=" + browser);
      session.setAttribute("browser", browser);
    } else {
      System.out.println("存在session，browser=" + sessionBrowser.toString());
    }
    Cookie[] cookies = request.getCookies();
    if (cookies != null && cookies.length > 0) {
      for (Cookie cookie : cookies) {
        System.out.println(cookie.getName() + " : " + cookie.getValue());
      }
    }

    String username = body.get("username");
    String password = body.get("password");

    UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
        username, password);
    authenticationManager.authenticate(authRequest);
    return "success";
  }
}
