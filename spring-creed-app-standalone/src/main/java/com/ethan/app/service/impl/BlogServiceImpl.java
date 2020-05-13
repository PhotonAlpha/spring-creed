package com.ethan.app.service.impl;

import com.ethan.app.dao.BlogDao;
import com.ethan.app.dto.BlogDTO;
import com.ethan.app.dto.BlogSearchConditionDTO;
import com.ethan.app.mapper.BlogMapper;
import com.ethan.app.service.BlogService;
import com.ethan.context.constant.ResponseEnum;
import com.ethan.context.vo.ResponseVO;
import com.ethan.entity.BlogDO;
import com.ethan.vo.BlogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class BlogServiceImpl implements BlogService {
  @Autowired
  private CacheManager cacheManager;

  private final BlogDao blogDao;
  private final BlogMapper blogMapper;

  public BlogServiceImpl(BlogDao blogDao, BlogMapper blogMapper) {
    this.blogDao = blogDao;
    this.blogMapper = blogMapper;
  }

  @Override
  @Cacheable(cacheNames = "cache_user", key = "'blog' + #root.methodName")
  public ResponseVO<List<BlogVO>> findByCondition(BlogSearchConditionDTO condition) {
    System.out.println("-----------> 未触发缓存");
    //TODO
    List<BlogDO> blogList = blogDao.findAll();
    List<BlogVO> result = blogMapper.blogListToVo(blogList);
    return ResponseVO.success(result);
  }

  @Override
  @Cacheable(cacheNames = "cache_blog", key = "'blog' + #root.methodName", unless = "#result == null ")
  public ResponseVO<List<BlogVO>> findByCondition1(BlogSearchConditionDTO condition) {
    System.out.println("-----------> 未触发缓存1");
    //TODO
    List<BlogDO> blogList = blogDao.findAll();
    List<BlogVO> result = blogMapper.blogListToVo(blogList);
    return ResponseVO.success(result);
  }

  @Override
  @CacheEvict(cacheNames = "cache_user", key = "'blogfindByCondition'")
  public ResponseVO<BlogVO> createBlog(BlogDTO blogDTO) {
    BlogDO blogDO = blogMapper.blogToDo(blogDTO);

    blogDao.save(blogDO);

    BlogVO result = blogMapper.blogToVo(blogDO);
    return ResponseVO.success(result);
  }

  @Override
  @CacheEvict(cacheNames = "cache_user", key = "'blogfindByCondition'")
  public ResponseVO updateBlog(Long id, BlogDTO blogDTO) {
    Optional<BlogDO> optional = blogDao.findById(id);
    if (optional.isPresent()) {
      BlogDO blogDO = optional.get();
      BlogDO pendingUpdateDO = blogMapper.blogToDo(blogDTO);
      pendingUpdateDO.setBlogId(blogDO.getBlogId());
      pendingUpdateDO.setBloggerId(blogDO.getBloggerId());
      blogDao.save(pendingUpdateDO);

      BlogVO result = blogMapper.blogToVo(pendingUpdateDO);
      return ResponseVO.success(result);
    } else {
      return ResponseVO.error(ResponseEnum.INCORRECT_PARAMS);
    }
  }

  @Override
  public ResponseVO deleteBlog(Long id) {
    Collection<String> cacheNames = cacheManager.getCacheNames();
    Cache cache = cacheManager.getCache("cache_user");
    cache.clear();

    Optional<BlogDO> optional = blogDao.findById(id);
    if (optional.isPresent()) {
      blogDao.delete(optional.get());
      return ResponseVO.success();
    } else {
      return ResponseVO.error(ResponseEnum.INCORRECT_PARAMS);
    }
  }

}
