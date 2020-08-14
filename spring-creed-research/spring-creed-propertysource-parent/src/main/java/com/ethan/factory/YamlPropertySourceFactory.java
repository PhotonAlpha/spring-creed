/*
 * System Name         : GEBNexGen
 * Program Id          : gebng-common
 * Program Description : GEBNG YAML Property Source Factory
 *
 * Revision History
 *
 * Date            Author             SR No           Description of Change
 * ----------      ----------         ------          ----------------------
 * 14-OCT-2018     DURAIRAJ Siva      Release2		  GEBNG YAML Property Source Factory
 *
 * Copyright (c) United Overseas Bank Limited Co.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * United Overseas Bank Limited Co. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with
 * United Overseas Bank Limited Co.
 */
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

/**
 * This factory class helps to load custom YAML file using @PropertySource
 *
 * @author DURAIRAJ Siva
 * @version 1.0
 */

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

