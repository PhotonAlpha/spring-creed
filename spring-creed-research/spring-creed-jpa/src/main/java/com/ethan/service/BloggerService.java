/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.service;

import com.ethan.entity.BloggerDO;
import com.ethan.vo.BloggerVO;

import java.util.List;

public interface BloggerService {

  BloggerDO save(BloggerDO aDo);

  List<BloggerVO> findAll();

  BloggerDO update(BloggerDO aDo);

  void delete(Long id);

  BloggerVO loadUserByUsername(String username);
}
