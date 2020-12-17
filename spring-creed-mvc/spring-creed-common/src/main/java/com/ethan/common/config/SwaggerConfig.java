package com.ethan.common.config;

import com.google.common.base.Predicates;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
// 此处有坑，参考README.md
//@EnableWebMvc
@ConditionalOnProperty(prefix = "swagger2", value = {"enable"}, havingValue = "true")
public class SwaggerConfig {
  @Value("${swagger2.basePackage:com.ethan.app.controller}")
  private String basePackage;
  @Value("${swagger2.pathRegex:/api*}")
  private String pathRegex;

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .select()
        .apis(RequestHandlerSelectors.basePackage(basePackage))
        .paths(Predicates.not(PathSelectors.regex(pathRegex)))
        .paths(PathSelectors.any())
        .build();
  }

  private ApiInfo apiInfo() {
    Contact contact = new Contact("Ethan Cao", "", "411084090@qq.com");
    return new ApiInfoBuilder()
        .title("Spring Boot Swagger")
        .description("Spring Boot Project")
        .version("1.0.0")
        .license("Apache 2.0")
        .contact(contact)
        .build();
  }
}
