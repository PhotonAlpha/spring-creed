package com.ethan.app.controller;

import com.ethan.app.dto.BlogDTO;
import com.ethan.app.service.BlogService;
import com.ethan.context.vo.ResponseVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api("博客相关接口")
@RestController
@RequestMapping("/api/v1")
public class BlogController {
  private final BlogService blogService;

  public BlogController(BlogService blogService) {
    this.blogService = blogService;
  }

  @ApiOperation("查询博客列表")
  @GetMapping("blogs")
  public ResponseEntity<ResponseVO> getBlogList() {
    return ResponseEntity.ok(blogService.findByCondition(null));
  }

  @ApiOperation("查询指定博客")
  @GetMapping("blogs/{id}")
  public ResponseEntity<ResponseVO> getBlog(@PathVariable("id") Long id) {
    return ResponseEntity.ok(blogService.findByCondition(null));
  }

  @ApiOperation("新增博客")
  @PostMapping("blogs")
  public ResponseEntity<ResponseVO> createBlog(@RequestBody BlogDTO blogDTO) {
    blogDTO = newBlogDTO();
    return ResponseEntity.ok(blogService.createBlog(blogDTO));
  }

  @ApiOperation("更新博客")
  @PutMapping("blogs/{id}")
  public ResponseEntity<ResponseVO> updateBlog(@PathVariable("id") Long id, @RequestBody BlogDTO blogDTO) {
    return ResponseEntity.ok(blogService.updateBlog(id, blogDTO));
  }

  @ApiOperation("删除博客")
  @DeleteMapping("blogs/{id}")
  public ResponseEntity<ResponseVO> deleteBlog(@PathVariable("id") Long id) {
    return ResponseEntity.ok(blogService.deleteBlog(id));
  }

  private BlogDTO newBlogDTO() {
    BlogDTO dto = new BlogDTO();
    dto.setBloggerId(1L);
    dto.setBloggerTitle("我的第一个博客");
    dto.setBlogContent("这是一个内容");
    dto.setBlogViews(100L);
    dto.setBlogCommentCount(20L);
    dto.setBlogLikes(20L);
    dto.setBlogPublishTime(new Date());
    return dto;
  }
}
