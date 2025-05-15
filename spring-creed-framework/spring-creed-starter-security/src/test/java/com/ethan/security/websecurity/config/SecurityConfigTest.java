package com.ethan.security.websecurity.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class SecurityConfigTest {
    @Test
    void passwordEncoderTest() {
        PasswordEncoder instance = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        System.out.println(instance.encode("password"));
        System.out.println(instance.matches("password", "{noop}password"));
    }

    @Test
    void pathSegmentTest() {

        UriComponents uriComponents = UriComponentsBuilder.fromUriString("http://localhost:8080/api/segment/get/test1").build();
        var key = uriComponents.getPathSegments().getLast();
        System.out.println(key);
    }
}
