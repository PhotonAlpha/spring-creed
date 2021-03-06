package com.ethan.app.service;

import com.ethan.entity.BloggerDO;
import com.ethan.vo.BloggerVO;

import java.util.List;

/**
 * 用户管理层
 */
public interface BloggerService {
  BloggerDO save(BloggerDO aDo);

  List<BloggerVO> findAll();

  BloggerDO update(BloggerDO aDo);

  void delete(Long id);

  BloggerVO loadUserByUsername(String username);
}
