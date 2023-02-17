/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.web.openapi.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
@ConditionalOnClass({OpenAPI.class, GroupedOpenApi.class})
// 允许使用 swagger.enable=false 禁用 Swagger
@ConditionalOnProperty(prefix = "ethan.swagger", value = "enable", matchIfMissing = true)
public class OpenApiAutoConfiguration {
    @Bean
    public OpenAPI api(OpenApiProperties properties) {
        return new OpenAPI()
                .info(apiInfo(properties))
                .externalDocs(
                        new ExternalDocumentation()
                                .url("https://springdoc.org/")
                                .description("Springdoc Openapi")
                )
                // ③ 安全上下文（认证）
                .components(components())
                // ④ 全局参数（多租户 header）

                ;
    }

    private Components components() {
        return new Components().addSecuritySchemes("bearer-key", new SecurityScheme().type(SecurityScheme.Type.OAUTH2).scheme("bearer").bearerFormat("JWT"))
                .parameters(globalRequestParameters());
    }

    public static final String HEADER_TENANT_ID = "tenant-id";

    private static Map<String, Parameter> globalRequestParameters() {
        var parameterMap = new HashMap<String, Parameter>();
        Parameter exampleParameter = new Parameter()
                .name(HEADER_TENANT_ID)
                .description("TENANT ID")
                .in(ParameterIn.HEADER.toString())
                .example(
                        new Example()
                                .value(1)
                );
        parameterMap.put(HEADER_TENANT_ID, exampleParameter);
        return parameterMap;
    }

    private Info apiInfo(OpenApiProperties properties) {
        return new Info()
                .title(properties.getTitle())
                .description(properties.getDescription())
                .contact(new Contact().name(properties.getAuthor()))
                .version(properties.getVersion());
    }

    @Bean
    public GroupedOpenApi publicApi(OpenApiProperties properties) {
        return GroupedOpenApi.builder()
                .group("dbs-psl-public")
                .packagesToScan(properties.getBasePackage())
                .build();
    }

}
