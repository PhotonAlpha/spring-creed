package com.ethan;

import com.ethan.service.AspectService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = LocalLockApplication.class)
public class AspectApplicationTest {
  @Autowired
  private AspectService aspectService;

  @Test
  public void testService() {
    User u = new User("xiaoming", "123", AuthorityUtils.createAuthorityList("ADMIN"));
    aspectService.findByCondition(u);
  }
}
