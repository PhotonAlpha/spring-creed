# `@EnableWebMvc`使用坑点解析: 
因为在整个swagger时，意外引入了`@EnableWebMvc`导致static资源无法获取，百思不得其解之时只能从源码看起。

在Spring Boot中使用 `@EnableWebMvc`时，请求会被相应的Controller处理，然而这时访问静态资源却出现了404。
`@EnableWebMvc`是使用注解方式快捷配置Spring Webmvc的一个注解。在使用时你可能会遇到以下问题：
- Spring Boot在application文件中的配置失效
- 在Spring Boot的自定义配置类加上`@EnableWebMvc`后，发现自动配置的静态资源路径（classpath:/META/resources/，classpath:/resources/，classpath:/static/，classpath:/public/）资源无法访问。

通过查看`@EnableWebMvc`的源码，可以发现该注解就是为了引入一个`DelegatingWebMvcConfiguration` 配置类，而`DelegatingWebMvcConfiguration`又继承于`WebMvcConfigurationSupport`。也就是说，如果我们使用`@EnableWebMvc`就相当于导入了`WebMvcConfigurationSupport`类，这个时候，Spring Boot的自动装配就不会发生了，我们能用的，只有`WebMvcConfigurationSupport`提供的若干个配置。其实不使用`@EnableWebMvc`注解也是可以实现配置`Webmvc`，只需要将配置类继承于`WebMvcConfigurationSupport`类即可。

当使用`@EnableWebMvc`时，加载的是**`WebMvcConfigurationSupport`**中的配置项。

当不使用`@EnableWebMvc`时，使用的是**`WebMvcAutoConfiguration`**引入的配置项。

查看一下`WebMvcAutoConfiguration` 的源码：

```java
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
@AutoConfigureAfter({ DispatcherServletAutoConfiguration.class,
		ValidationAutoConfiguration.class })
public class WebMvcAutoConfiguration {
	...
}
```

可以看到自动配置类 WebMvcAutoConfiguration 上有条件注解
```java
@ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
```
这个注解的意思是在项目类路径中缺少 WebMvcConfigurationSupport类型的bean时改自动配置类才会生效。

有时候我们需要自己定制一些项目的设置，可以有以下几种使用方式：

- @EnableWebMvc+extends WebMvcConfigurationAdapter，在扩展的类中重写父类的方法即可，这种方式会屏蔽springboot的@EnableAutoConfiguration中的设置

- extends WebMvcConfigurationSupport，在扩展的类中重写父类的方法即可，这种方式会屏蔽springboot的@EnableAutoConfiguration中的设置

- extends WebMvcConfigurationAdapter/WebMvcConfigurer，在扩展的类中重写父类的方法即可，这种方式依旧使用springboot的@EnableAutoConfiguration中的设置

WebMvcConfigurer 没有暴露高级设置，如果需要高级设置 需要第二种方式继承WebMvcConfigurationSupport或者DelegatingWebMvcConfiguration，例如：
```java
@Configuration
@ComponentScan(basePackageClasses = { MyConfiguration.class })
public class MyConfiguration extends WebMvcConfigurationSupport {

   @Override
   public void addFormatters(FormatterRegistry formatterRegistry) {
       formatterRegistry.addConverter(new MyConverter());
   }

   @Bean
   public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
       // Create or delegate to "super" to create and
       // customize properties of RequestMappingHandlerAdapter
   }
}
```

所以无论是使用`@EnableWebMvc`还是`WebMvcConfigurationSupport`，都会禁止Spring Boot的自动装配`@EnableAutoConfiguration`中的设置( 虽然禁止了Spring boot的自动装配，但是`WebMvcConfigurationSupport`本身，还是会注册一系列的MVC相关的bean的)。

`@EnableAutoConfiguration`是SpringBoot项目的启动类注解`@SpringBootApplication`的子元素，`@EnableAutoConfiguration`实际是导入`EnableAutoConfigurationImportSelector`和`Registar`两个类，主要功能是通过`SpringFactoriesLoader.loadFactoryNames()`导入jar下面配置文件`META-INF/spring.factories`。我们翻一番spring.factories，其中就包含WebMvc自动装配类：
```properties
...
# Auto Configure
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration,\
...
```
并且@EnableAutoConfiguration 注解，会自动读取 application.properties 或 application.yml 文件中的配置。

如果想要使用自动配置生效，同时又要使用部分spring mvc默认配置的话，比如增加 viewController ，则可以将自己的配置类可以继承 `WebMvcConfigurerAdapter` 这个类。不过在Spring5.0版本WebMvcConfigurerAdapter 后这个类被标记为`@Deprecated`了 。原因是：

Spring 5.0后要使用Java8，而在Java8中接口是可以有default方法的，所以这个类就没必要了。所以我们只需要在自定义配置类中直接实现 WebMvcConfigurer 接口就好了。
```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MyWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/hello").setViewName("helloworld");
    }
}
```
这个时候我们使用thymeleaf就可以生效了

如果使用`@EnableWebMvc`了，那么就会自动覆盖了官方给出的`/static, /public, META-INF/resources, /resources`等存放静态资源的目录。而将静态资源定位于`src/main/webapp`。当需要重新定义好资源所在目录时，则需要主动添加上述的那个配置类，来重写 `addResourceHandlers`方法。
```java
@Configuration
@EnableWebMvc
public class MyWebConfig implements WebMvcConfigurer {
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	   registry.addResourceHandler("/**")
	           .addResourceLocations("classpath:/public/");
	}
}
```
从上述可知在SpringBoot中大多数时我们并不需要使用`@EnableWebMvc`注解，来看下官方的一段说明：
> Spring Boot provides auto-configuration for Spring MVC that works well with most applications.
  If you want to keep Spring Boot MVC features and you want to add additional MVC configuration (interceptors, formatters, view controllers, and other features), you can add your own @Configuration class of type WebMvcConfigurer but without @EnableWebMvc.
  If you want to take complete control of Spring MVC, you can add your own @Configuration annotated with @EnableWebMvc.  
>
> https://docs.spring.io/spring-boot/docs/2.0.0.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-auto-configuration

说明：
- Spring Boot 默认提供Spring MVC 自动配置，不需要使用`@EnableWebMvc`注解
- 如果需要配置MVC（拦截器、格式化、视图等） 请使用添加`@Configuration`并实现`WebMvcConfigurer`接口.不要添加`@EnableWebMvc`注解。
- `@EnableWebMvc` 只能添加到一个`@Configuration`配置类上，用于导入Spring Web MVC configuration

最后，如果Spring Boot在classpath里看到有 spring webmvc 也会自动添加`@EnableWebMvc`。

> Normally you would add @EnableWebMvc for a Spring MVC app, but Spring Boot adds it automatically when it sees spring-webmvc on the classpath. This flags the application as a web application and activates key behaviors such as setting up a DispatcherServlet.
