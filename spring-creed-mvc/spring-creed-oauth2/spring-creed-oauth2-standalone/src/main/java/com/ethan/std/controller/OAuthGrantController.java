package com.ethan.std.controller;

import com.ethan.std.config.ServerConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.ClientTokenServices;
import org.springframework.security.oauth2.client.token.JdbcClientTokenServices;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.sql.DataSource;
import java.security.Principal;
import java.util.Map;

/**
 * 参考 {@link org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator}
 */
@RestController
@RequestMapping("/auth")
public class OAuthGrantController {
  private ServerConfig serverConfig;

  @Bean
  public ClientTokenServices clientTokenServices(DataSource dataSource) {
    return new JdbcClientTokenServices(dataSource);
  }

  private ClientTokenServices tokenServices;


  @GetMapping("/grant")
  public OAuth2AccessToken grant(Principal principal, @RequestParam Map<String, String> parameters) {
    AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
    String code = parameters.get("code");
    resourceDetails.setAccessTokenUri("http://localhost:8080/oauth/token");
    resourceDetails.setClientId("clientapp");
    resourceDetails.setClientSecret("112233");
    // <2> 创建 OAuth2RestTemplate 对象
    OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
    AccessTokenRequest accessTokenRequest = restTemplate.getOAuth2ClientContext().getAccessTokenRequest();
    accessTokenRequest.setAuthorizationCode(code);
    accessTokenRequest.setPreservedState("http://localhost:8090/callback");

    AuthorizationCodeAccessTokenProvider tokenProvider = new AuthorizationCodeAccessTokenProvider();
    // tokenProvider.

    restTemplate.setAccessTokenProvider(new AuthorizationCodeAccessTokenProvider());
    // <3> 获取访问令牌
    OAuth2AccessToken accessToken = restTemplate.getAccessToken();
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    tokenServices.saveAccessToken(resourceDetails, authentication, accessToken);
    tokenServices.getAccessToken(resourceDetails, authentication);

    return accessToken;
  }
}
