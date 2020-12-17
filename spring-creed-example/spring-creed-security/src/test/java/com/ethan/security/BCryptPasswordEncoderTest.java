package com.ethan.security;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class BCryptPasswordEncoderTest {
  @Test
  void testBCryptPasswordEncoder() {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    String pwd = "123456";
    String ePwd = bCryptPasswordEncoder.encode(pwd);
    log.info("ePwd is :{}", ePwd);
    boolean match = bCryptPasswordEncoder.matches(pwd, ePwd);
    boolean success = bCryptPasswordEncoder.upgradeEncoding(ePwd);
    // $2a$10$6jS8S8u.Fh3ijZvyHjhAbeb3PK2MnQea8BJ1PapF9u3AYpmHEgMNq
    log.info("ePwd match :{}", match);
    log.info("upgradeEncoding success :{}", success);

  }
}
