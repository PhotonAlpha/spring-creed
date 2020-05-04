package com.ethan.datasource.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

//@Configuration
public class HibernateConfig {
  @Autowired
  private JpaProperties jpaProperties;

  @Bean
  public HibernateProperties hibernateProperties() {
    return new HibernateProperties();
  }

  /**
   * 获取 Hibernate Vendor 相关配置
   */
  @Bean(name = "hibernateVendorProperties")
  public Map<String, Object> hibernateVendorProperties() {
    return hibernateProperties().determineHibernateProperties(
        jpaProperties.getProperties(), new HibernateSettings());
  }
}
