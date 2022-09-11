package com.ethan.std.provider;

import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/10/2022 4:59 PM
 * OAuth2RestTemplate 并发访问的情况下，解决获取身份混乱问题
 */
public class SafeOAuth2ClientContext implements OAuth2ClientContext, Serializable {

    private static final long serialVersionUID = -1334135616343251192L;

    private ThreadLocal<OAuth2AccessToken> accessToken = new ThreadLocal<>();

    private AccessTokenRequest accessTokenRequest;

    private Map<String, Object> state = new ConcurrentHashMap<>();

    public SafeOAuth2ClientContext() {
        this(new DefaultAccessTokenRequest());
    }

    public SafeOAuth2ClientContext(AccessTokenRequest accessTokenRequest) {
        this.accessTokenRequest = accessTokenRequest;
    }

    public SafeOAuth2ClientContext(OAuth2AccessToken accessToken) {
        this.accessToken.set(accessToken);
        this.accessTokenRequest = new DefaultAccessTokenRequest();
    }

    @Override
    public OAuth2AccessToken getAccessToken() {
        return accessToken.get();
    }

    @Override
    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.accessToken.set(accessToken);
        this.accessTokenRequest.setExistingToken(accessToken);
    }

    @Override
    public AccessTokenRequest getAccessTokenRequest() {
        return accessTokenRequest;
    }

    @Override
    public void setPreservedState(String stateKey, Object preservedState) {
        state.put(stateKey, preservedState);
    }

    @Override
    public Object removePreservedState(String stateKey) {
        return state.remove(stateKey);
    }
}
