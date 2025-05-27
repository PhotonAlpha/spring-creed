# experimental features

>
> The module expects that org.springframework.boot:spring-boot-starter-actuator and org.springframework.boot:spring-boot-starter-aopare already provided at runtime. If you are using webflux with Spring Boot 2 or Spring Boot 3, you also need io.github.resilience4j:resilience4j-reactor
> 
> [docs/getting-started-3](https://resilience4j.readme.io/docs/getting-started-3)

- [ ] resilience4j-circuitbreaker: Circuit breaking
- [ ] resilience4j-ratelimiter: Rate limiting
- [ ] resilience4j-bulkhead: Bulkheading
- [ ] resilience4j-retry: Automatic retrying (sync and async)
- [ ] resilience4j-cache: Result caching
  会创建默认的retry： io.github.resilience4j.spring6.retry.configure.RetryAspect#getOrCreateRetry
- [ ] resilience4j-timelimiter: Timeout handling


[Reactor 3 参考指南](https://easywheelsoft.github.io/reactor-core-zh/index.html)