package com.ethan.security.websecurity.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityConfigTest {
    @Test
    void passwordEncoderTest() {
        PasswordEncoder instance = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        System.out.println(instance.encode("password"));
        System.out.println(instance.matches("password", "{noop}password"));
    }
}
