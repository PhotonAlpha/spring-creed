package com.ethan;

import com.ethan.entity.UserDO;
import com.ethan.mapper.UserMapper;
import org.assertj.core.api.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Resource;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = MybatisApplication.class)
public class SampleTest {
  @Resource
  private UserMapper userMapper;

  @Test
  public void testSelect() {
    System.out.println(("----- selectAll method test ------"));
    List<UserDO> userList = userMapper.selectList(null);
    Assertions.assertEquals(5, userList.size());
    userList.forEach(System.out::println);
  }
}
