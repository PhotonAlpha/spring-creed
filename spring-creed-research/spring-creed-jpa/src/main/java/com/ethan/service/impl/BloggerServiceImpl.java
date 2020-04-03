/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2020/04/02
 */
package com.ethan.service.impl;

import com.ethan.dao.BloggerDao;
import com.ethan.dao.GroupDao;
import com.ethan.dao.RoleDao;
import com.ethan.entity.AuthorityEnum;
import com.ethan.entity.BloggerDO;
import com.ethan.entity.GroupDO;
import com.ethan.entity.GroupEnum;
import com.ethan.entity.RoleDO;
import com.ethan.service.BloggerService;
import com.ethan.snow.Snowflake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class BloggerServiceImpl implements BloggerService {
  private final AtomicInteger atomicInteger = new AtomicInteger(1);
  @Resource
  private BloggerDao bloggerDao;
  @Resource
  private RoleDao roleDao;
  @Resource
  private GroupDao groupDao;

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
      RoleDO authority = Arrays.asList(adminRole, bloggerRole, moderatorRole).get(index);
      List<GroupDO> randomBloggerGroup = pickNRandom(groups, index);


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
