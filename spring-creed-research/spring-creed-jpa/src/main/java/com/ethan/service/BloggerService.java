/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.service;

import com.ethan.entity.BloggerDO;
import org.springframework.data.domain.Page;

public interface BloggerService {

  BloggerDO save(BloggerDO aDo);

  Page<BloggerDO> findAll();

  BloggerDO update(BloggerDO aDo);

  void delete(Long id);
}
