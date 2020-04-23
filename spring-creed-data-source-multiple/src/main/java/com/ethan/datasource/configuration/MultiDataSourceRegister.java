package com.ethan.datasource.configuration;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.sql.DataSource;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * //TODO
 * https://fastdep.louislivi.com/#/module/fastdep-datasource
 */
@Slf4j
public class MultiDataSourceRegister implements EnvironmentAware, ImportBeanDefinitionRegistrar {
  private static Map<String, Object> registerBean = new ConcurrentHashMap<>();
  private Environment env;
  private Binder binder;

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    MultiDataSourceProperties multipleDataSources;
    try {
      multipleDataSources = binder.bind("multi.datasource", MultiDataSourceProperties.class).get();
    } catch (NoSuchElementException e) {
      log.error("Failed to configure fastDep DataSource: 'fastdep.datasource' attribute is not specified and no embedded datasource could be configured.");
      return;
    }

    for (Map.Entry<String, MDataSourceProperties> entry : multipleDataSources.getDatasource().entrySet()) {
      String nickname = entry.getKey();
      MDataSourceProperties properties = entry.getValue();
      // datasource
      Supplier<DataSource> dataSourceSupplier = () -> {
        //获取注册数据
        AtomikosDataSourceBean registerDataSource = (AtomikosDataSourceBean) registerBean.get(nickname + "DataSource");
        if (registerDataSource != null) {
          return registerDataSource;
        }
        registerDataSource = new AtomikosDataSourceBean();
        registerDataSource.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        //registerDataSource.setUniqueResourceName(key);
        //registerDataSource.setMinPoolSize(fastDepDataSource.getMinIdle());
        //registerDataSource.setMaxPoolSize(fastDepDataSource.getMaxActive());
        //registerDataSource.setBorrowConnectionTimeout((int) fastDepDataSource.getTimeBetweenEvictionRunsMillis());
        //registerDataSource.setMaxIdleTime((int) fastDepDataSource.getMaxEvictableIdleTimeMillis());
        //registerDataSource.setTestQuery(fastDepDataSource.getValidationQuery());
        //registerDataSource.setXaDataSource(fastDepDataSource);
        //registerBean.put(key + "DataSource", registerDataSource);
        return registerDataSource;
      };
    }
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.env = environment;
    // bing binder
    binder = Binder.get(this.env);
  }
}
