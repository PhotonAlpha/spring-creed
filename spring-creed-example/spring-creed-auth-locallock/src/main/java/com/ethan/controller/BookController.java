package com.ethan.controller;

import com.ethan.annotation.CacheLock;
import com.ethan.annotation.CacheParam;
import com.ethan.annotation.LocalLock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
public class BookController {
  @LocalLock(key = "book:arg[0]")
  @GetMapping("local")
  public String query(@RequestParam String token) {
    return "success - " + token;
  }
  @CacheLock
  @GetMapping("cache")
  public String queryCache(@CacheParam(name = "token") @RequestParam String token) {
    return "success - " + token;
  }
}
