/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.service.impl;

import com.ethan.dao.BloggerDao;
import com.ethan.entity.BloggerDO;
import com.ethan.service.BloggerService;
import com.ethan.snow.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BloggerServiceImpl implements BloggerService {
  private final AtomicInteger atomicInteger = new AtomicInteger(1);
  @Resource
  private BloggerDao bloggerDao;

  @PostConstruct
  public void onInit() {
    Snowflake snowflake = new Snowflake(1L);
    int i = 0;
    do {
      int identity = atomicInteger.getAndIncrement();

      BloggerDO aDo = BloggerDO.builder().name("user" + identity)
          .nickName(snowflake.nextId() + "")
          .email(identity + "@ethan.com").build();
      bloggerDao.save(aDo);
      i++;
    } while (i < 10);

  }

  @Override
  public BloggerDO save(BloggerDO aDo) {
    return bloggerDao.save(aDo);
  }

  @Override
  public Page<BloggerDO> findAll() {
    // Sort sort = new Sort(Sort.Direction.ASC, "nickName");
    // PageRequest pageable = PageRequest.of(0, 5, sort);
    PageRequest pageable = PageRequest.of(0, 5);
    Page<BloggerDO> result = bloggerDao.findAll(pageable);
    return result;
  }

  @Override
  public BloggerDO update(BloggerDO aDo) {
    return bloggerDao.save(aDo);
  }

  @Override
  public void delete(Long id) {
    BloggerDO blogger = bloggerDao.getOne(id);
    bloggerDao.delete(blogger);
  }
}
