package com.ethan.std.example;

import com.ethan.std.vo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/1/2022 5:47 PM
 */
@RestController
@RequestMapping("/api/demo")
public class TokenDemoController {
    // @Autowired
    // private DefaultTokenServices tokenServices;
    // @Autowired
    // private InMemoryTokenStore tokenStore;

    @PostMapping("/revoke")
    public String revokeToken(@RequestParam("token") String token) {
        // OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        // OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(token);

        // boolean b = tokenServices.revokeToken(token);
        // return b;
        return token;
    }
    @PostMapping("/xss")
    public Person revokeToken(@RequestBody Person person) {
        return person;
    }

}
