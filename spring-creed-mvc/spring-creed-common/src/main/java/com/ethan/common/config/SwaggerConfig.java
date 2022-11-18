package com.ethan.common.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "springdoc.api-docs", value = {"enable"}, havingValue = "true")
public class SwaggerConfig {
  @Value("${springdoc.basePackage:com.ethan.app.controller}")
  private String basePackage;
  @Value("${springdoc.pathRegex:/api*}")
  private String pathRegex;

  @Bean
  public OpenAPI api() {
    return new OpenAPI()
            .info(
                    new Info()
                            .title("Spring Creed Swagger")
                            .description("Spring Creed Project for rest api")
                            .version("1.0")
                            .license(new License().name("Apache 2.0").url("https://springdoc.org"))
            )
            .externalDocs(
                    new ExternalDocumentation()
                            .description("Spring Creed Project")
            );
  }

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
            .group("spring-creed-public")
            .packagesToScan(basePackage)
            // .pathsToMatch("/public/**")
            .build();
  }
  @Bean
  public GroupedOpenApi adminApi() {
    return GroupedOpenApi.builder()
            .group("spring-creed-admin")
            // .pathsToMatch("/admin/**")
            .pathsToMatch(pathRegex)
            // .addOpenApiMethodFilter(method -> method.isAnnotationPresent(Admin.class))
            .build();
  }
}
