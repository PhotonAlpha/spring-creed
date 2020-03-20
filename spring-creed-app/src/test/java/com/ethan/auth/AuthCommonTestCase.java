package com.ethan.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class AuthCommonTestCase {
  @Test
  @DisplayName("should")
  void testJackson() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    DefaultOAuth2AccessToken auth2AccessToken = new DefaultOAuth2AccessToken("123");
    DefaultExpiringOAuth2RefreshToken refreshToken = new DefaultExpiringOAuth2RefreshToken("123", new Date());
    auth2AccessToken.setRefreshToken(refreshToken);
    String res = mapper.writeValueAsString(auth2AccessToken);
    System.out.println(res);
  }

  @Test
  void name() {
    for (int i = 0; i < 10; i++) {
      Random random = new Random(System.currentTimeMillis());
      int res = random.nextInt(5);
      long seconds = LocalDateTime.now().plusHours(5)
          .minus(System.currentTimeMillis(), ChronoUnit.MILLIS)
          .toEpochSecond(ZoneId.systemDefault().getRules().getOffset(Instant.now()));
      System.out.println(seconds);
    }
  }
}
