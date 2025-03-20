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
> https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#simplediscoveryclient
> 
> 4. @LoadBalancerClient or @LoadBalancerClients 添加@Configuration 导致异常
>
>       The classes you pass as @LoadBalancerClient or @LoadBalancerClients configuration arguments should either not be annotated with @Configuration or be outside component scan scope.
>       https://docs.spring.io/spring-cloud-commons/docs/current/reference/html/#custom-loadbalancer-configuration
> 
```yaml
spring:
  cloud:
    discovery:
      client:
        simple:
          instances:
            creed-cache-cluster:
              - secure: true
                port: 8088
                host: 127.0.0.1
                instanceId: creed-cache-cluster-1
              - secure: true
                port: 8089
                host: 127.0.0.1
                instanceId: creed-cache-cluster-2
```
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


Non issue: https://stackoverflow.com/questions/77727371/java-io-eofexception-null

> 2024-07-30 15:03:18.724 [GST] DEBUG [GSTGW,,] 7827 [-exec-7] o.a.coyote.http11.Http11Processor   [175] : Error state [CLOSE_CONNECTION_NOW] reported while processing request
> java.io.EOFException: null
> 	at org.apache.tomcat.util.net.NioEndpoint$NioSocketWrapper.fillReadBuffer(NioEndpoint.java:1288)


# 部署 static index.html，并解决刷新问题 
```java
@Controller
public class IndexController {
    // 仅匹配前端 处理刷新问题
    // @RequestMapping({"/{path:[^\\\\.]*}"})
    @RequestMapping({"/index", "/system/*"})
    public String index() {
        return "forward:/";
    }
}
```

```xml
<build>
   <resources>
       <resource>
           <directory>src/main/resources</directory>
           <filtering>true</filtering>
           <excludes>
               <exclude>templates/*.xlsx</exclude>
               <exclude>static/**</exclude>
           </excludes>
       </resource>
       <resource>
           <directory>src/main/resources</directory>
           <filtering>false</filtering>
           <includes>
               <include>templates/*.xlsx</include>
               <include>static/**</include>
           </includes>
       </resource>
   </resources>
</build>
```


# 记一次缺少bean 导致启动陷入死循环问题

因为报错导致Springboot启动陷入死循环

> org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.security.config.annotation.method.configuration.PrePostMethodSecurityConfiguration': No bean named 'org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry' available

查看日志发现以下log

从上面我们可以看到`org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext`@271f18d3has not been refreshed yet，那么是不是ApplicationCOntext.refresh()报出了异常，我们来具体查看下源码:
查看源码 `org.springframework.context.support.AbstractApplicationContext#refresh`
找到该方法打印异常日志的地方,异常打印关键字:`Exception encountered during context initialization`,这个异常会打印出具体的异常信息。

```log
10-09-2024 18:14:31.267 DEBUG [main][,] o.s.b.f.s.DefaultListableBeanFactory    [835] : Failed to resolve cached argument

org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'org.springframework.security.config.annotation.method.configuration.PrePostMethodSecurityConfiguration': No bean named 'org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry' available
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:607)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:205)
	at org.springframework.beans.factory.support.ConstructorResolver$ConstructorDependencyDescriptor.resolveShortcut(ConstructorResolver.java:1440)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1363)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353)
	at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:904)
	at org.springframework.beans.factory.support.ConstructorResolver.resolvePreparedArguments(ConstructorResolver.java:824)
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:448)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1337)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1167)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:562)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:205)
	at org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper.findAdvisorBeans(BeanFactoryAdvisorRetrievalHelper.java:91)
	at org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator.findCandidateAdvisors(AbstractAdvisorAutoProxyCreator.java:111)
	at org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator.findCandidateAdvisors(AnnotationAwareAspectJAutoProxyCreator.java:92)
	at org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator.shouldSkip(AspectJAwareAdvisorAutoProxyCreator.java:101)
	at org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator.postProcessBeforeInstantiation(AbstractAutoProxyCreator.java:281)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInstantiation(AbstractAutowireCapableBeanFactory.java:1130)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.resolveBeforeInstantiation(AbstractAutowireCapableBeanFactory.java:1105)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.getSingletonFactoryBeanForTypeCheck(AbstractAutowireCapableBeanFactory.java:994)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.getTypeForFactoryBean(AbstractAutowireCapableBeanFactory.java:891)
	at org.springframework.beans.factory.support.AbstractBeanFactory.isTypeMatch(AbstractBeanFactory.java:663)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doGetBeanNamesForType(DefaultListableBeanFactory.java:575)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanNamesForType(DefaultListableBeanFactory.java:542)
	at org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors(BeanFactoryUtils.java:260)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.findAutowireCandidates(DefaultListableBeanFactory.java:1637)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1397)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.autowireResource(CommonAnnotationBeanPostProcessor.java:598)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.getResource(CommonAnnotationBeanPostProcessor.java:576)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor$ResourceElement.getResourceToInject(CommonAnnotationBeanPostProcessor.java:738)
	at org.springframework.beans.factory.annotation.InjectionMetadata$InjectedElement.inject(InjectionMetadata.java:270)
	at org.springframework.beans.factory.annotation.InjectionMetadata.inject(InjectionMetadata.java:145)
	at org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.postProcessProperties(CommonAnnotationBeanPostProcessor.java:368)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1421)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:599)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522)
	at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337)
	at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200)
	at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:409)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1337)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1167)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.getSingletonFactoryBeanForTypeCheck(AbstractAutowireCapableBeanFactory.java:996)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.getTypeForFactoryBean(AbstractAutowireCapableBeanFactory.java:891)
	at org.springframework.beans.factory.support.AbstractBeanFactory.isTypeMatch(AbstractBeanFactory.java:663)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.doGetBeanNamesForType(DefaultListableBeanFactory.java:575)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanNamesForType(DefaultListableBeanFactory.java:542)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanNamesForType(DefaultListableBeanFactory.java:519)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanNamesForType(DefaultListableBeanFactory.java:512)
	at org.springframework.beans.factory.BeanFactoryUtils.beanNamesForTypeIncludingAncestors(BeanFactoryUtils.java:163)
	at org.springframework.boot.autoconfigure.diagnostics.analyzer.NoSuchBeanDefinitionFailureAnalyzer.getUserConfigurationResults(NoSuchBeanDefinitionFailureAnalyzer.java:135)
	at org.springframework.boot.autoconfigure.diagnostics.analyzer.NoSuchBeanDefinitionFailureAnalyzer.analyze(NoSuchBeanDefinitionFailureAnalyzer.java:81)
	at org.springframework.boot.autoconfigure.diagnostics.analyzer.NoSuchBeanDefinitionFailureAnalyzer.analyze(NoSuchBeanDefinitionFailureAnalyzer.java:59)
	at org.springframework.boot.diagnostics.analyzer.AbstractInjectionFailureAnalyzer.analyze(AbstractInjectionFailureAnalyzer.java:40)
	at org.springframework.boot.diagnostics.AbstractFailureAnalyzer.analyze(AbstractFailureAnalyzer.java:34)
	at org.springframework.boot.diagnostics.FailureAnalyzers.analyze(FailureAnalyzers.java:88)
	at org.springframework.boot.diagnostics.FailureAnalyzers.reportException(FailureAnalyzers.java:81)
	at org.springframework.boot.SpringApplication.reportFailure(SpringApplication.java:841)
	at org.springframework.boot.SpringApplication.handleRunFailure(SpringApplication.java:814)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:345)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1363)
	at org.springframework.boot.SpringApplication.run(SpringApplication.java:1352)
	at com.ethan.server.ServerApplication.main(ServerApplication.java:28)
Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry' available
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanDefinition(DefaultListableBeanFactory.java:895)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getMergedLocalBeanDefinition(AbstractBeanFactory.java:1362)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:300)
	at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:205)
	at org.springframework.context.annotation.ConfigurationClassPostProcessor$ImportAwareBeanPostProcessor.postProcessBeforeInitialization(ConfigurationClassPostProcessor.java:569)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyBeanPostProcessorsBeforeInitialization(AbstractAutowireCapableBeanFactory.java:422)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1780)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600)
	... 72 common frames omitted

10-09-2024 18:59:11.837  WARN [main][,] ConfigServletWebServerApplicationContext[632] : Exception encountered during context initialization - cancelling refresh attempt: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'redisMessageListenerContainer' defined in class path resource [com/ethan/mq/config/RedisMQAutoConfiguration.class]: Unsatisfied dependency expressed through method 'redisMessageListenerContainer' parameter 1: Error creating bean with name 'menuRefreshConsumer': Injection of resource dependencies failed
```
通过添加以下配置，启动懒加载机制，这时候启动就会失败，正常提示是什么BEAN导致的启动失败了
```yaml
spring:
     main:
       lazy-initialization: true
```
通过排查发现 缺少了 ConfigApi，添加 `@ConditionalOnMissingBean` 使用默认的bean, 解决问题！

## 错误描述:Caused by: org.springframework.beans.factory.BeanNotOfRequiredTypeException: Bean named '****' is expected to be of type '****' but was actually of type 'com.sun.proxy.$Proxy**'的两种解决方法

一次偶然情况下遇到两次这样的问题,两次用下述不同的方法解决的.

这个错误的原因是spring aop代理混用的问题,如果想要了解什么是Spring aop代理与为什么会混乱,自己去百度吧,我还是直奔主题哈,相信大家解决问题心切.

原因： **在java中默认使用的动态代理是JDK proxy基于接口的代理， 如果你报错上述信息，极有可能是你注入的时候，注入到了类上面**，如下:

public class TestA implements BaseTest

即testA类继承了BaseTest，但是如果你在自动注入到了类上，如下:

@Autowired
TestA testA；

就可能出现上述问题！ 可以修改为

@Autowired
BaseTest testA；

这种，基于接口的注入，应该可以解决；

如果还是不行的话，就用下述中的方案解决，不过相当于修改了 代理方式，其实也影响不大.

```yaml
spring:
  aop:
    proxy-target-class: true
```

# tmp folder issue

- https://stackoverflow.com/questions/50523407/the-temporary-upload-location-tmp-tomcat-4296537502689403143-5000-work-tomcat/50523578
- https://javaee.github.io/tutorial/servlets011.html
- https://github.com/spring-projects/spring-boot/issues/9073