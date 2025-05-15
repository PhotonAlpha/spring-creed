/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springframework.context.annotation.Bean;

// @Configuration
public class OpenConfig {
    @Bean
    public OpenAPI springShopOpenAPI() {
        final String locUrl = "http://localhost:8080";
        final String devUrl = "https://.de";
        final String testUrl = "https://.de";
        final String preUrl = "https://.de";
        final String proUrl = "https://.grp";




        return new OpenAPI()
                // .info(new Info().title("SpringShop API")
                //         .description("Spring shop sample application")
                //         .version("v0.0.1")
                //         .license(new License().name("Apache 2.0").url("http://springdoc.org")))

                .addServersItem(new Server().url(locUrl))
                .addServersItem(new Server().url(devUrl))
                .addServersItem(new Server().url(testUrl))
                .addServersItem(new Server().url(preUrl))
                .addServersItem(new Server().url(proUrl))
                .info(
                        new Info().version("v1").title("XApp application API")
                                .description("(NOTE: If having Swagger UI issues w/ Chrome then use Firefox instead.)")
                                .version("v0.0.1")
                                .contact(new Contact().name("Edi")))

                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }

    /**
     * #springdoc:
     * #  show-actuator: true
     * #  swagger-ui:
     * #    path: /swagger-ui.html
     * #    url: /v3/api-docs
     * #    disable-swagger-default-url: true
     * @param properties
     * @return
     */
    // @Bean
    public SwaggerUiConfigProperties swaggerUiConfigProperties(SwaggerUiConfigProperties properties) {
        properties.setPath("/swagger-ui.html");
        properties.setConfigUrl("/v3/api-docs");
        properties.setDisableSwaggerDefaultUrl(true);
        return properties;
    }
   /* @Bean
   public OpenAPI api() {
       return new OpenAPI()
               .info(
                       new Info()
                               .title("PSL Rest APIs")
                               .description("PSL internal Rest APIs")
                               .version("1.0")
               )
               .externalDocs(
                       new ExternalDocumentation()
                               .description("PSL rest apis for PSL BPMN workflow")
               );
   }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("dbs-psl-public")
                .packagesToScan("com.ethan")
                .build();
    } */
}
