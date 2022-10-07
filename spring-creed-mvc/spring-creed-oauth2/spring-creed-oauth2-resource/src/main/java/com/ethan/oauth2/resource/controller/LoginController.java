package com.ethan.oauth2.resource.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2ClientProperties;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 7/29/2022 3:20 PM
 */
@RestController
@RequestMapping("/")
public class LoginController {
    @Autowired
    private OAuth2ClientProperties oauth2ClientProperties;
    @Value("${security.oauth2.client.access-token-uri}")
    private String accessTokenUri;
    @PostMapping("login")
    public OAuth2AccessToken login(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {
        // <1> 创建 ResourceOwnerPasswordResourceDetails 对象
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setAccessTokenUri("http://localhost:8080/oauth/token");
        resourceDetails.setClientId(oauth2ClientProperties.getClientId());
        resourceDetails.setClientSecret(oauth2ClientProperties.getClientSecret());
        resourceDetails.setUsername(username);
        resourceDetails.setPassword(password);
        // <2> 创建 OAuth2RestTemplate 对象
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
        restTemplate.setAccessTokenProvider(new ResourceOwnerPasswordAccessTokenProvider());
        // <3> 获取访问令牌
        return restTemplate.getAccessToken();
    }
    @PostMapping("client-login")
    public OAuth2AccessToken clientLogin() {
        // <1> 创建 ResourceOwnerPasswordResourceDetails 对象
        ClientCredentialsResourceDetails resourceDetails = new ClientCredentialsResourceDetails();
        resourceDetails.setAccessTokenUri("http://localhost:8080/oauth/token");
        resourceDetails.setClientId("client_4");
        resourceDetails.setClientSecret(oauth2ClientProperties.getClientSecret());
        // <2> 创建 OAuth2RestTemplate 对象
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
        restTemplate.setAccessTokenProvider(new ClientCredentialsAccessTokenProvider());
        // <3> 获取访问令牌
        return restTemplate.getAccessToken();
    }
}
