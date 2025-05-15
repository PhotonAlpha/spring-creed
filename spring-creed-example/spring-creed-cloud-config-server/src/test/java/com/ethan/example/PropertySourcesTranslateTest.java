package com.ethan.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * YAML Properties 互相转换测试类
 * @author EthanCao
 * @description spring-creed
 * @date 30/4/25
 */
public class PropertySourcesTranslateTest {

    @Test
    void yamlConverter() throws IOException {
        YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
        List<PropertySource<?>> load = loader.load("application-client", new ClassPathResource("application-client.yml"));
        for (var propertySource : load) {
            if (propertySource instanceof  MapPropertySource src) {
                Map<String, Object> sourceProperties = src.getSource();
                sourceProperties.entrySet().forEach(System.out::println);
            }
        }
    }

    @Test
    void propertiesConverter() throws IOException {
        PropertiesPropertySourceLoader loader = new PropertiesPropertySourceLoader();
        List<PropertySource<?>> load = loader.load("application-client", new ClassPathResource("application-client.properties"));
        for (var propertySource : load) {
            if (propertySource instanceof  MapPropertySource src) {
                Map<String, Object> sourceProperties = src.getSource();
                sourceProperties.entrySet().forEach(System.out::println);
            }
        }
    }
    @Test
    void properties2YamlConverter() throws IOException {
        JavaPropsMapper propsMapper = new JavaPropsMapper();
        JsonNode jsonNode = propsMapper.readTree(new ClassPathResource("application-client.properties").getInputStream());
        YAMLFactory yamlFactory = new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)  //removes marker characters ---
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .disable(YAMLGenerator.Feature.ALWAYS_QUOTE_NUMBERS_AS_STRINGS)  //avoid numbers being quote
                .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR);  //list array elements by prefixing hifen


        ObjectMapper yamlMapper = new ObjectMapper(yamlFactory);
        // ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

        var ymlString = yamlMapper.writeValueAsString(jsonNode);
        // 转换为 YAML 文件
        // yamlMapper.writeValue(new File(
        //         "/Users/spring-creed-cloud-config-server/src/main/resources/templates/application.yml"),
        //         jsonNode);
        System.out.println("======ymlString======");
        System.out.println(ymlString);
    }


}
