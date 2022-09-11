package com.ethan.oauth2.resource.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 7/29/2022 5:02 PM
 */
@RestController
public class CallbackController {
    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;
    @Autowired
    private OAuth2ClientProperties oauth2ClientProperties;



    @GetMapping("/callback")
    public OAuth2AccessToken getOAuth2AccessToken(@RequestParam("code") String code) {
        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
        resourceDetails.setAccessTokenUri("http://localhost:8080/oauth/token");
        resourceDetails.setClientId("client_2");
        resourceDetails.setClientSecret(oauth2ClientProperties.getClientSecret());
        // <2> 创建 OAuth2RestTemplate 对象
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
        AccessTokenRequest accessTokenRequest = restTemplate.getOAuth2ClientContext().getAccessTokenRequest();
        accessTokenRequest.setAuthorizationCode(code);
        accessTokenRequest.setPreservedState("http://localhost:8090/callback");
        restTemplate.setAccessTokenProvider(new AuthorizationCodeAccessTokenProvider());
        // <3> 获取访问令牌
        return restTemplate.getAccessToken();
    }
    @GetMapping("/callback2")
    public String getOAuth2AccessToken2(@RequestParam("code") String code) {
        return "this is a page";
    }
}
