package com.ethan.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BCryptPasswordEncoderTest {
  @Test
  void testBCryptPasswordEncoder() {
    BCryptPasswordEncoder cryptPasswordEncoder = new BCryptPasswordEncoder();
    String adminStr = cryptPasswordEncoder.encode("admin");
    String normalStr = cryptPasswordEncoder.encode("normal");
    System.out.println(adminStr);
    System.out.println(normalStr);

  }

  @Test
  void testRegex() {
    String sqlFragment = "USD22.77123SGD123.669";
    Pattern pattern = Pattern.compile("((?i)USD)\\d+\\.\\d{2}");
    Matcher matcher = pattern.matcher(sqlFragment);
    //循环，字符串中有多少个符合的，就循环多少次
    while(matcher.find()){
      String e = matcher.group();
      System.out.println("result:" + e);
    }
  }
}
