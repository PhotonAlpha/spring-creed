package com.ethan.std.provider;

import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/8/2022 4:38 PM
 */
@Deprecated
public class CustomizeAuthenticationKeyGenerator extends DefaultAuthenticationKeyGenerator {
    private static final String CLIENT_ID = "client_id";

    public static final String TOKEN = "access_token";

    private static final String SCOPE = "scope";

    private static final String USERNAME = "username";

    @Override
    public String extractKey(OAuth2Authentication authentication) {
        Map<String, String> values = new LinkedHashMap<>();

        // authentication.get
        OAuth2Request authorizationRequest = authentication.getOAuth2Request();
        if (!authentication.isClientOnly()) {
            values.put(USERNAME, authentication.getName());
        }
        values.put(CLIENT_ID, authorizationRequest.getClientId());

        // Map<String, Serializable> extensions = authorizationRequest.getExtensions();
        // if (extensions != null && extensions.containsKey(TOKEN)) {
        //     String tokenVal = (String) extensions.get(TOKEN);
        //     values.put(TOKEN, tokenVal);
        // }

        if (authorizationRequest.getScope() != null) {
            values.put(SCOPE, OAuth2Utils.formatParameterList(new TreeSet<>(authorizationRequest.getScope())));
        }
        return generateKey(values);
    }
}
