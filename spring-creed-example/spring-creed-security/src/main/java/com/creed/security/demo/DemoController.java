package com.creed.security.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
  @GetMapping("rest1/v1/test/hello")
  public String helloWord() {
    return "helloWord1";
  }
  @GetMapping("rest1/v1/test/world")
  public String world() {
    return "world1";
  }

  @GetMapping("rest2/v1/test/hello")
  public String helloWord2() {
    return "helloWord2";
  }
  @GetMapping("rest2/v1/test/world")
  public String world2() {
    return "world2";
  }

  @GetMapping("rest3/v1/test/hello")
  public String helloWord3() {
    return "helloWord3";
  }
  @GetMapping("rest3/v1/test/world")
  public String world3() {
    return "world3";
  }
}
