package com.ethan.server;

import com.ethan.server.config.MessageSourcesConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;
import org.junit.jupiter.api.Disabled;

@SpringBootTest(classes = ServerApplication.class)
@Disabled("依赖本地数据库/中间件，Spring 上下文无法启动，CI 暂禁用")
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
