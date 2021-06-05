package com.creed.controller;

import com.creed.config.ServerConfig;
import com.creed.constant.UrlEnum;
import com.creed.vo.ResponseVO;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.util.StringUtils;

import java.security.Principal;
import java.util.Map;

/**
 * 参考 {@link org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator}
 */
@RestController
@RequestMapping("/auth")
public class OAuthGrantController {
  private final ServerConfig serverConfig;
  private final RestTemplate restTemplate;

  public OAuthGrantController(ServerConfig serverConfig, RestTemplate restTemplate) {
    this.serverConfig = serverConfig;
    this.restTemplate = restTemplate;
  }


  @GetMapping("/grant")
  public ResponseEntity<ResponseVO> grant(Principal principal, @RequestParam Map<String, String> parameters) {

    String code = parameters.get("code");

    if (StringUtils.isEmptyOrWhitespace(code)) {

    }
    MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
    paramMap.add("code", code);
    paramMap.add(OAuth2Utils.CLIENT_ID, UrlEnum.AUTHORIZATION.getClientId());
    paramMap.add("client_secret", UrlEnum.AUTHORIZATION.getClientSecret());
    paramMap.add(OAuth2Utils.GRANT_TYPE, UrlEnum.AUTHORIZATION.getGrantType());
    paramMap.add(OAuth2Utils.REDIRECT_URI, UrlEnum.AUTHORIZATION.getRedirectUri());
    paramMap.add(OAuth2Utils.SCOPE, "read_userinfo read_contacts");
    UriComponents uriComponents = UriComponentsBuilder.fromUriString(serverConfig.getUrl() + UrlEnum.LOGIN_URL.getUrl())
        .queryParams(paramMap).build();
    HttpEntity<String> body= new HttpEntity<>("");

    ResponseEntity<DefaultOAuth2AccessToken> accessToken = null;
    try {
      accessToken = restTemplate.postForEntity(uriComponents.toUri(), body, DefaultOAuth2AccessToken.class);
    } catch (RestClientException e) {
      return ResponseVO.errorParams(e.getMessage()).build();
    } catch (OAuth2Exception e) {
      return ResponseVO.error(e).build();
    }
    return ResponseVO.success(accessToken.getStatusCode(), accessToken.getBody()).build();
  }
}
