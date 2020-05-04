package com.ethan.datasource.configuration;

import com.ethan.datasource.constant.DBConstants;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

//@Configuration
//@EnableJpaRepositories(
//    entityManagerFactoryRef = DBConstants.ENTITY_MANAGER_FACTORY_USERS,
//    transactionManagerRef = DBConstants.TX_MANAGER_USERS,
//    basePackages = {"com.ethan.datasource.dao.user"}
//)
public class JpaUserConfig {
  @Resource(name = "hibernateVendorProperties")
  private Map<String, Object> hibernateVendorProperties;

  @Bean
  @ConfigurationProperties("spring.shardingsphere.datasource.ds-users")
  public DataSourceProperties usersDataSourceProperties() {
    return new DataSourceProperties();
  }

  /**
   * 创建 orders 数据源
   */
  @Bean(name = "usersDataSource")
  public DataSource dataSource() {
    return usersDataSourceProperties().initializeDataSourceBuilder()
        .type(HikariDataSource.class).build();
  }

  /**
   * 创建 LocalContainerEntityManagerFactoryBean
   */
  @Bean(name = DBConstants.ENTITY_MANAGER_FACTORY_USERS)
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(this.dataSource()) // 数据源
        .properties(hibernateVendorProperties) // 获取并注入 Hibernate Vendor 相关配置
        .packages("com.ethan.datasource.model.user") // 数据库实体 entity 所在包
        .persistenceUnit("usersPersistenceUnit") // 设置持久单元的名字，需要唯一
        .build();
  }

  /**
   * 创建 PlatformTransactionManager
   */
  @Bean(name = DBConstants.TX_MANAGER_USERS)
  public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
    return new JpaTransactionManager(entityManagerFactory(builder).getObject());
  }
}
