package com.ethan.server;

import com.ethan.server.config.MessageSourcesConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

@SpringBootTest(classes = ServerApplication.class)
public class InternationalizationApplicationTest {
    @Autowired
    @Qualifier("resourcesMessageSource")
    MessageSource messageSource;
    @Autowired
    MessageSourcesConfiguration messageSourcesConfiguration;

    @Test
    void messageSourceTest() {
        System.out.println(messageSource.getMessage("header.tail", null, Locale.ENGLISH));
    }
}
