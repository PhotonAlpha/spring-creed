package com.ethan.example;

import com.ethan.example.controller.vo.PropertiesDto;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Disabled;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 30/4/25
 */
@SpringBootTest
@Disabled("Spring 上下文无法启动，CI 暂禁用")
public class TemplateTranslateTest {

    @Resource(name = "txtSpringTemplateEngine")
    private SpringTemplateEngine springTemplateEngine;

    @Test
    void templateEngine() throws IOException {
        List<PropertiesDto> systemDicts = new ArrayList<>();

        Context context = new Context();
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        List<PropertySource<?>> load = loader.load("application-client", new ClassPathResource("application-client.yml"));
        for (var propertySource : load) {
            if (propertySource instanceof  MapPropertySource src) {
                Map<String, Object> sourceProperties = src.getSource();
                for (Map.Entry<String, Object> entry : sourceProperties.entrySet()) {
                    if (entry.getValue() instanceof OriginTrackedValue otv) {
                        systemDicts.add(
                                new PropertiesDto("spring-cloud-config-client", "local", "master", entry.getKey(), otv.getValue() + "")
                        );
                    }

                }
            }
        }

        context.setVariable("dataProperties", systemDicts);
        String result = springTemplateEngine.process("insert-source-data", context);
        System.out.println("=====result===");
        System.out.println(result);

    }

}
