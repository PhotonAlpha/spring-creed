/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.controller;

import com.ethan.entity.BloggerDO;
import com.ethan.service.BloggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BloggerController {

  @Autowired
  private BloggerService bloggerService;

  @GetMapping("/blog/all")
  public ResponseEntity<Page> getAll() {

    Page<BloggerDO> result = bloggerService.findAll();
    return ResponseEntity.ok(result);
  }

}
