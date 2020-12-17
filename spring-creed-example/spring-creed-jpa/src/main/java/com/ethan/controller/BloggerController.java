/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.controller;

import com.ethan.service.BloggerService;
import com.ethan.vo.BloggerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BloggerController {

  @Autowired
  private BloggerService bloggerService;

  @GetMapping("/blog/all")
  public ResponseEntity<List> getAll() {

    List<BloggerVO> result = bloggerService.findAll();
    return ResponseEntity.ok(result);
  }

  @GetMapping("/blog")
  public ResponseEntity<BloggerVO> getAll(@Param("username") String username) {

    BloggerVO result = bloggerService.loadUserByUsername(username);
    return ResponseEntity.ok(result);
  }

}
