package com.ethan.datasource.configuration;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MultiDataSourceProperties {
  private Map<String, MDataSourceProperties> datasource;
}
