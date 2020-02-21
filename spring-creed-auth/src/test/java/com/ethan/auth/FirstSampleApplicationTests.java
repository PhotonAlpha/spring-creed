package com.ethan.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringbootSecurityAuth2Application.class)
@TestPropertySource(locations = "classpath:application.yml")
public class FirstSampleApplicationTests {

  @Test
  public void contextLoads() {

  }

}
