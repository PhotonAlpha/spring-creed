package com.creed.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/session")
public class SessionController {

  @GetMapping("/set") // 其实 PostMapping 更合适，单纯为了方便
  public void set(HttpSession session,
                  @RequestParam("key") String key,
                  @RequestParam("value") String value) {
    session.setAttribute(key, value);
  }

  @GetMapping("/get_all")
  public Map<String, Object> getAll(HttpServletRequest request) {
    Map<String, Object> result = new HashMap<>();
    HttpSession session = request.getSession();
    // 遍历
    for (Enumeration<String> enumeration = session.getAttributeNames();
         enumeration.hasMoreElements();) {
      String key = enumeration.nextElement();
      Object value = session.getAttribute(key);
      result.put(key, value);
    }
    // 返回
    return result;
  }

}
