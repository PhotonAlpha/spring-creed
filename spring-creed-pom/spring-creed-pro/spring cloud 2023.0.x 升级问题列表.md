## spring cloud 2023.0.x 升级问题列表

> [!NOTE]
>
> 1. 懒加载bean： -Dspring.main.lazy-initialization=true
>
> 2. Boot 3.x you need to use Micrometer Tracing instead of Sleuth： https://stackoverflow.com/questions/74998310/spring-cloud-sleuth-with-spring-boot-3-0-and-tracer
> 原来的traceId实现方式： https://github.com/spring-cloud/spring-cloud-sleuth/blob/76ad931b4298b18389d0a4ea739316c14682888b/spring-cloud-sleuth-autoconfigure/src/main/java/org/springframework/cloud/sleuth/autoconfig/TraceEnvironmentPostProcessor.java#L47-L48
> 只需要在application.yml添加如下配置
> 
>     ```yaml
>     logging:
>          pattern:
>               level: %5p [${spring.zipkin.service.name:${spring.application.name:}},%X{traceId:-},%X{spanId:-}]
>     ```
>
> 3. Component scan 源码： ClassPathScanningCandidateComponentProvider
     >
     >    ```java
>    @ComponentScan(basePackages = {"com.creed", "com.netflix.client.config"},
>            excludeFilters = {
>                    @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.creed.*.*Application", "com.creed.CommonComponentConfig"})
>            })
>    ```
>
>
>    ClassPathScanningCandidateComponentProvider#isCandidateComponent
>
> spring cloud custom loadbalancer
> https://spring.io/guides/gs/spring-cloud-loadbalancer
> 
> 4. @LoadBalancerClient or @LoadBalancerClients 添加@Configuration 导致异常
>   
>
>       The classes you pass as @LoadBalancerClient or @LoadBalancerClients configuration arguments should either not be annotated with @Configuration or be outside component scan scope.
>       https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#custom-loadbalancer-configuration
> 
> 5. caffeine cache key为null, 无法正确的从参数中获取#name的值 
> 
>
>       Using deprecated '-debug' fallback for parameter name resolution. Compile the affected code with '-parameters' instead
>      fix cache key is null issue: https://github.com/spring-projects/spring-framework/wiki/Upgrading-to-Spring-Framework-6.x#parameter-name-retention
>      https://stackoverflow.com/questions/24301074/spring-cacheable-with-ehcache-spel-find-null-for-valid-object
>      https://stackoverflow.com/questions/22959459/spelevaluationexception-el1007epos-43-field-or-property-group-cannot-be-f
>      https://stackoverflow.com/questions/74600681/warning-printed-after-migrating-to-spring-boot-3-0-spring-integration-6-0
> 
> 6. lombok code coverage issue
> 
>         use @lombok.XXX instead of importing import lombok.Getter;
> 
>         It really solve the problem。
> 
>         but ，after my test,
> 
>         upgrade the lombok to org.projectlombok:lombok:1.18.26+
> 
>         the problem alse can be solved.
> 
>    https://stackoverflow.com/questions/65412697/addlombokgeneratedannotation-on-lombok-config-doesnt-ignore-lombok-annotations
> 7. 



