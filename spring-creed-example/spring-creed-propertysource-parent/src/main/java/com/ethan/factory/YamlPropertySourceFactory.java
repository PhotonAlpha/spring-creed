package com.ethan.factory;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.lang.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(@Nullable String name, EncodedResource resource) throws IOException {
    Properties propFromYaml = loadYamlIntoProperties(resource);
    String sourceName = name != null ? name : resource.getResource().getFilename();
    return new PropertiesPropertySource(sourceName, propFromYaml);
  }

  private Properties loadYamlIntoProperties(EncodedResource encodedResource) throws FileNotFoundException {
    try {
      YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
      factory.setResources(encodedResource.getResource());
      factory.afterPropertiesSet();
      return factory.getObject();
    } catch (IllegalStateException ex) {
      Throwable cause = ex.getCause();
      if (cause instanceof FileNotFoundException)
        throw (FileNotFoundException) ex.getCause();
      throw ex;
    }
  }
}

