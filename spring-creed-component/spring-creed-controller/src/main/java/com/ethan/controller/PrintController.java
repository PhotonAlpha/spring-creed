package com.ethan.controller;

import com.ethan.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/test")
public class PrintController extends BasicController {
  @Autowired
  private ConfigService configService;

  @GetMapping(value = "/content/{name}")
  public ResponseEntity<List> getContent(@PathVariable("name") String name) {
    return ResponseEntity.ok(configService.put(name));
  }
  @GetMapping(value = "/play")
  public ResponseEntity<String> getContent() {
    return ResponseEntity.ok(configService.play(1L, "dic", "maker"));
  }
}
