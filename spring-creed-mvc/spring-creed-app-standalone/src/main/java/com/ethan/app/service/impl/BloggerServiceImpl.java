package com.ethan.app.service.impl;

import com.ethan.app.dao.BloggerDao;
import com.ethan.app.dao.GroupDao;
import com.ethan.app.dao.RoleDao;
import com.ethan.app.service.BloggerService;
import com.ethan.entity.AuthorityEnum;
import com.ethan.entity.BloggerDO;
import com.ethan.entity.GroupDO;
import com.ethan.entity.GroupEnum;
import com.ethan.entity.RoleDO;
import com.ethan.mapper.BloggerMapper;
import com.ethan.snow.Snowflake;
import com.ethan.vo.BloggerVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BloggerServiceImpl implements BloggerService {
  private final AtomicInteger atomicInteger = new AtomicInteger(1);
  @Resource
  private BloggerDao bloggerDao;
  @Resource
  private RoleDao roleDao;
  @Resource
  private GroupDao groupDao;

  @Autowired
  private BloggerMapper bloggerMapper;

  @PostConstruct
  public void onInit() {
    // 设置博客角色
    RoleDO adminRole = RoleDO.builder().roleName(AuthorityEnum.ADMIN).build();
    RoleDO bloggerRole = RoleDO.builder().roleName(AuthorityEnum.BLOGGER).build();
    RoleDO moderatorRole = RoleDO.builder().roleName(AuthorityEnum.MODERATOR).build();
    RoleDO supervisorRole = RoleDO.builder().roleName(AuthorityEnum.SUPERVISOR).build();
    List<RoleDO> roles = Arrays.asList(adminRole, bloggerRole, moderatorRole, supervisorRole);
    roleDao.saveAll(roles);

    GroupDO g1 = GroupDO.builder().groupName(GroupEnum.SUPERVISOR).roles(Arrays.asList(supervisorRole)).build();
    // 风纪委员会 可以是版主 管理员
    GroupDO g2 = GroupDO.builder().groupName(GroupEnum.DISCIPLINE_MEMBERS).roles(Arrays.asList(adminRole, moderatorRole)).build();
    // 版主
    GroupDO g3 = GroupDO.builder().groupName(GroupEnum.MODERATOR).roles(Arrays.asList(moderatorRole)).build();
    // 子版主
    GroupDO g4 = GroupDO.builder().groupName(GroupEnum.SUB_MODERATOR).roles(Arrays.asList(moderatorRole)).build();
    List<GroupDO> groups = Arrays.asList(g1, g2, g3, g4);
    groupDao.saveAll(Arrays.asList(g1, g2, g3, g4));


    Snowflake snowflake = new Snowflake(1L);
    Random ran = new Random();
    int i = 0;
    do {
      int index = ran.nextInt(3);
      int gIndex = ran.nextInt(4);
      RoleDO authority = Arrays.asList(adminRole, bloggerRole, moderatorRole).get(index);
      List<GroupDO> randomBloggerGroup = pickNRandom(groups, gIndex);


      int identity = atomicInteger.getAndIncrement();

      BloggerDO aDo = BloggerDO.builder().name("user" + identity)
          .nickName(snowflake.nextId() + "")
          .email(identity + "@ethan.com")
          .roles(Arrays.asList(authority))
          .groups(randomBloggerGroup)
          .build();
      bloggerDao.save(aDo);
      i++;
    } while (i < 10);

  }

  public static <T>List<T> pickNRandom(List<T> lst, int n) {
    List<T> copy = new LinkedList<T>(lst);
    Collections.shuffle(copy);
    return copy.subList(0, n);
  }

  @Override
  public BloggerDO save(BloggerDO aDo) {
    return bloggerDao.save(aDo);
  }

  @Override
  public List<BloggerVO> findAll() {
    // Sort sort = new Sort(Sort.Direction.ASC, "nickName");
    // PageRequest pageable = PageRequest.of(0, 5, sort);
    PageRequest pageable = PageRequest.of(0, 5);
    Page<BloggerDO> result = bloggerDao.findAll(pageable);
    log.info("pagination current:{} total page:{} total size:{}", result.getNumber(),
        result.getTotalPages(), result.getTotalElements());
    List<BloggerVO> vos = result.getContent().stream().map(bloggerMapper :: bloggerToVo).collect(Collectors.toList());

    return vos;
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

  @Override
  public BloggerVO loadUserByUsername(String username) {
    BloggerDO blogger = bloggerDao.loadUserByUsername(username);
    return bloggerMapper.bloggerToVo(blogger);
  }
}
