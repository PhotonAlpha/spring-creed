package com.ethan.datasource.configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MDataSourceProperties extends org.springframework.boot.autoconfigure.jdbc.DataSourceProperties {
  private HikariProperties hikari;
}
