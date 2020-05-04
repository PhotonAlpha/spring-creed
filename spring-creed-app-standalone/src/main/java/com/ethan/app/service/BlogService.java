package com.ethan.app.service;

import com.ethan.app.dto.BlogDTO;
import com.ethan.app.dto.BlogSearchConditionDTO;
import com.ethan.context.vo.ResponseVO;
import com.ethan.vo.BlogVO;

import java.util.List;

/**
 * 博客管理层
 */
public interface BlogService {
  ResponseVO<List<BlogVO>> findByCondition(BlogSearchConditionDTO condition);

  ResponseVO<BlogVO> createBlog(BlogDTO blogDTO);

  ResponseVO updateBlog(Long id, BlogDTO blogDTO);

  ResponseVO deleteBlog(Long id);
}
