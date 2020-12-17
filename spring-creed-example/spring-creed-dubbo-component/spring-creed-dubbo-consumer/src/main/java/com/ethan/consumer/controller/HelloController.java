package com.ethan.consumer.controller;

import com.ethan.HelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  //@Reference(version = "${demo.service.version}", url = "dubbo://127.0.0.1:20880")
  @Reference(version = "${demo.service.version}")
  private HelloService helloService;

  @GetMapping("hello")
  public String SayHello(String name) {
    return helloService.sayHello(name);
  }
}
