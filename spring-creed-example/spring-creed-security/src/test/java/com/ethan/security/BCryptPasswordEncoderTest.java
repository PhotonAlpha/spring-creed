package com.ethan.security;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderTest {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(BCryptPasswordEncoderTest.class);

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
