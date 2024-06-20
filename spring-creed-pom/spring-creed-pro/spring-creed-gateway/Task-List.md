
- [x] 创建webflux API
- [x] 整合Spring security，实现API认证
- [ ] 请求数据缓存
- [x] 思考在gateway中数据聚合如何实现
- [x] 思考路由根据不同条件调用不同的路由，实现数据聚合.//TODO 基本实现了，具体需要在实际环境中实验
- 思考 Global Filters（全局过滤器） 与  Filters（过滤器）不同与应用场景
- [ ] 实现 负载均衡 [ref](https://spring.io/guides/gs/spring-cloud-loadbalancer)
- [ ] 实现 retry
- [x] 实现 circuit breaker服务熔断
- [ ] 实现 Rate limit 限流
- [x] 实现**全链路跟踪**, [解决多线程traceID为空的问题](https://stackoverflow.com/questions/78020101/spring-boot-3-upgrade-executor-context-propagation-sleuth-to-micrometer-tracing)
- [ ] 实现 Observability，集成 grafana prometheus //TODO 参考[spring-creed-starter-monitor](..%2Fspring-creed-framework%2Fspring-creed-starter-monitor)

> https://www.cnblogs.com/myitnews/p/14095560.html
> - 性能：API高可用，负载均衡，容错机制。
> - 安全：权限身份认证、脱敏，流量清洗，后端签名（保证全链路可信调用）,黑名单（非法调用的限制）。
> - 日志：日志记录（spainid,traceid）一旦涉及分布式，全链路跟踪必不可少。
> - 缓存：数据缓存。
> - 监控：记录请求响应数据，api耗时分析，性能监控。
> - 限流：流量控制，错峰流控，可以定义多种限流规则。
> - 灰度：线上灰度部署，可以减小风险。
> - 路由：动态路由规则。
>
 

参考文章： https://github.com/ThomasVitale/spring-cloud-gateway-resilience-security-observability

大佬视频：https://www.youtube.com/watch?v=SyM6moBYL7s
