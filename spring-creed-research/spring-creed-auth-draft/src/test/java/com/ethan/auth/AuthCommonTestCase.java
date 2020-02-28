package auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;

import java.util.Date;


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

}
