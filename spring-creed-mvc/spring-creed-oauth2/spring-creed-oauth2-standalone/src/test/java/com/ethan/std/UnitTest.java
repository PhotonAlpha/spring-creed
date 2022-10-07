package com.ethan.std;

import com.ethan.std.utils.DateUtil;
import com.ethan.std.utils.InstanceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 4:17 PM
 */
public class UnitTest {
    @Test
    void testPwd() {
        DelegatingPasswordEncoder passwordEncoder = (DelegatingPasswordEncoder)PasswordEncoderFactories.createDelegatingPasswordEncoder();
        passwordEncoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());

        String encodedPwd = passwordEncoder.encode("abc");
        System.out.println("encoded pwd is:" + encodedPwd);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss:SSS");
        System.out.println(dateTimeFormatter.format(ZonedDateTime.now()));
        // zdt.toInstant().toEpochMilli();
        ZonedDateTime zonedDateTime = ZonedDateTime.parse("2022-08-18T17:52:29:675", dateTimeFormatter.withZone(ZoneId.systemDefault()));
        System.out.println("zonedDateTime:" + zonedDateTime);
        boolean expired = DateUtil.expired(zonedDateTime.toInstant().toEpochMilli() + "", 60);
        System.out.println("expired:" + expired);

    }

    @Test
    void testAlograthon() throws IOException {
        // OAuthNonceServices
        String value = IOUtils.toString(new ClassPathResource("test.json").getInputStream(), StandardCharsets.UTF_8);
        // String value = "abcdsaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
            // digest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
        }

        try {
            byte[] bytes = digest.digest(value.getBytes("UTF-8"));
            String result = String.format("%032x", new BigInteger(1, bytes));
            System.out.println("result:" + result);
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
        }
    }


}
