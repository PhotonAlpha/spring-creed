package com.ethan.service;

import com.ethan.annotation.Cacheable;
import com.ethan.context.vo.ResponseVO;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class AspectService {
  @Cacheable(value = "people", key = "#user.username", depict = "用户信息缓存")
  public ResponseVO<User> findByCondition(User user) {
    return ResponseVO.success(user);
  }
}
