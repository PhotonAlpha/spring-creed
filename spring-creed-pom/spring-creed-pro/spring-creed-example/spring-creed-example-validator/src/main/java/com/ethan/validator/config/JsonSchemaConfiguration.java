package com.ethan.validator.config;

import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "json-schema-validator")
public class JsonSchemaConfiguration {
    private boolean cache = true;

    @Bean
    @ConditionalOnMissingBean
    public JsonSchemaFactory jsonSchemaFactory() {
        return JsonSchemaFactory
                .getInstance(SpecVersion.VersionFlag.V202012, builder -> builder
                        .schemaMappers(
                                mappers -> mappers.mapPrefix("https://www.example.org/", "classpath:schema/")
                        ).enableSchemaCache(cache)
                );
    }
}
