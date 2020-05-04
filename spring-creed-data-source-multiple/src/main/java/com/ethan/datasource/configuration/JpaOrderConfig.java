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
//    entityManagerFactoryRef = DBConstants.ENTITY_MANAGER_FACTORY_ORDERS,
//    transactionManagerRef = DBConstants.TX_MANAGER_ORDERS,
//    basePackages = {"com.ethan.datasource.dao.order"}
//)
public class JpaOrderConfig {
  @Resource(name = "hibernateVendorProperties")
  private Map<String, Object> hibernateVendorProperties;

  @Bean
  @Primary
  @ConfigurationProperties("spring.shardingsphere.datasource.ds-orders")
  public DataSourceProperties ordersDataSourceProperties() {
    return new DataSourceProperties();
  }

  /**
   * 创建 orders 数据源
   */
  @Bean(name = "ordersDataSource")
  @Primary // 需要特殊添加，否则初始化会有问题
  public DataSource dataSource() {
    return ordersDataSourceProperties().initializeDataSourceBuilder()
        .type(HikariDataSource.class).build();
  }

  /**
   * 创建 LocalContainerEntityManagerFactoryBean
   */
  @Primary
  @Bean(name = DBConstants.ENTITY_MANAGER_FACTORY_ORDERS)
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
    return builder
        .dataSource(this.dataSource()) // 数据源
        .properties(hibernateVendorProperties) // 获取并注入 Hibernate Vendor 相关配置
        .packages("com.ethan.datasource.model.order") // 数据库实体 entity 所在包
        .persistenceUnit("ordersPersistenceUnit") // 设置持久单元的名字，需要唯一
        .build();
  }

  /**
   * 创建 PlatformTransactionManager
   */
  @Bean(name = DBConstants.TX_MANAGER_ORDERS)
  public PlatformTransactionManager transactionManager(EntityManagerFactoryBuilder builder) {
    return new JpaTransactionManager(entityManagerFactory(builder).getObject());
  }

}
