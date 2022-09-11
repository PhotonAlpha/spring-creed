package com.ethan.oauth2.controller;

import com.ethan.oauth2.vo.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.error.DefaultOAuth2ExceptionRenderer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 7/27/2022 3:22 PM
 */
@RestController
public class TestEndpoints {
    private static final Logger log = LoggerFactory.getLogger(TestEndpoints.class);
    @GetMapping("/product/{id}")
    public String getProduct(@PathVariable String id) {
        //for debug
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication:{}", authentication);
        return "product id :" + id;
    }
    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable String id) {
        //for debug
        // DefaultOAuth2ExceptionRenderer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication:{}", authentication);
        return "order id :" + id;
    }
    @PostMapping(value = "/product/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> gettoken(@RequestBody Student user) {
        //for debug
        // DefaultOAuth2ExceptionRenderer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication:{}", authentication);
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String val = LocalDateTime.now().toString();
        return ResponseEntity.ok("{\"token\": \""+val+"\"}");
    }

    @PostMapping("/login")
    public OAuth2AccessToken login(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {
        // <1> 创建 ResourceOwnerPasswordResourceDetails 对象
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setAccessTokenUri("http://localhost:8080/oauth/token");
        resourceDetails.setClientId("clientapp");
        resourceDetails.setClientSecret("112233");
        resourceDetails.setUsername(username);
        resourceDetails.setPassword(password);
        // <2> 创建 OAuth2RestTemplate 对象
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
        restTemplate.setAccessTokenProvider(new ResourceOwnerPasswordAccessTokenProvider());
        // <3> 获取访问令牌
        return restTemplate.getAccessToken();
    }
}
