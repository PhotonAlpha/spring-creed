package com.ethan.core.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
  /**
   * Algorithm Reference: https://github.com/auth0/java-jwt/blob/master/lib/src/main/java/com/auth0/jwt/algorithms/Algorithm.java
   * @return JWTVerifier
   * @Deprecated because of have more powerful tools
   */
  /*@Bean
  public JWTVerifier jwtVerifier() {
    Algorithm algorithm = Algorithm.HMAC512(secret);
    return JWT.require(algorithm)
        .withIssuer(issuer)
        .acceptLeeway(leeway)
        .build();
  }*/
}
