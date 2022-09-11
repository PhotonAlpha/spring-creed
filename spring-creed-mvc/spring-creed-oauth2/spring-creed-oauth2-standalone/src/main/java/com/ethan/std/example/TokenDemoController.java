package com.ethan.std.example;

import com.ethan.std.provider.CustomizeTokenServices;
import com.ethan.std.vo.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
    @Autowired
    private CustomizeTokenServices tokenServices;
    private TokenExtractor tokenExtractor = new BearerTokenExtractor();

    private AccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();

    @PostMapping("/revoke")
    public String revokeToken(@RequestParam("token") String token) {
        // OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(token);
        // OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(token);

        // boolean b = tokenServices.revokeToken(token);
        // return b;
        return token;
    }
    @GetMapping("/check_token")
    public Map<String, Object> checkToken(HttpServletRequest request) {
         Authentication extract = tokenExtractor.extract(request);
        OAuth2AccessToken token = tokenServices.readAccessToken((String)extract.getPrincipal());
        if (token == null) {
            throw new InvalidTokenException("Token was not recognised");
        }

        if (token.isExpired()) {
            throw new InvalidTokenException("Token has expired");
        }

        /*OAuth2Authentication authentication = tokenServices.loadAuthentication(token.getValue()); */
        OAuth2Authentication authentication = (OAuth2Authentication)SecurityContextHolder.getContext().getAuthentication();
        // String token = (String) authentication.getPrincipal();
        Map<String, Object> response = (Map<String, Object>)accessTokenConverter.convertAccessToken(token, authentication);

        // gh-1070
        response.put("active", true);	// Always true if token exists and not expired

        return response;
    }
    @PostMapping("/xss")
    public Person revokeToken(@RequestBody Person person) {
        return person;
    }

}
