package com.ethan.config;

import com.ethan.factory.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@PropertySources(value = {
        @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:property-context.yml"),
        @PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:property-context-${country_code}.yml", ignoreResourceNotFound = true)
})
@ConfigurationProperties(prefix = "rules")
@EnableConfigurationProperties
@Getter
@Setter
public class ExportProperties {

    @Value("${gng-export-ms-cluster.protocol}")
    private String exportWrapperProtocol;

    String reportTemplatePath;
}
