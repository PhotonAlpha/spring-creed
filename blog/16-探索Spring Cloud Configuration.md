# 探索Spring Cloud Configuration

org.springframework.cloud.config.server.environment.EnvironmentController



```java
// org.springframework.cloud.config.server.config.JdbcRepositoryConfiguration.JdbcRepositoryConfiguration
@Configuration(proxyBeanMethods = false)
@Profile("jdbc")
@ConditionalOnClass(JdbcTemplate.class)
@ConditionalOnProperty(value = "spring.cloud.config.server.jdbc.enabled", matchIfMissing = true)
class JdbcRepositoryConfiguration {

	@Bean
	@ConditionalOnBean(JdbcTemplate.class)
	public JdbcEnvironmentRepository jdbcEnvironmentRepository(JdbcEnvironmentRepositoryFactory factory,
			JdbcEnvironmentProperties environmentProperties) {
		return factory.build(environmentProperties);
	}

}
```



