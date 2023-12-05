<!--/*@thymesVar id="company" type="com.ethan.system.pdf.controller.dto.Company"*/-->
<!--👆👆上述用法解决解析Intellij提示问题👆👆 @{see https://youtrack.jetbrains.com/issue/IDEA-290739/Thymeleaf-the-thymesVar-comment-for-fields-of-a-fragment-Object-param-intermittent-uncertainty-does-not-work} -->
<!--👇👇当此fragment继续嵌套子fragment的时候，子fragment需要使用以下注释 👇👇 @{see https://stackoverflow.com/questions/37774928/is-there-a-way-to-pass-parameters-to-thymeleaf-includes} -->
<!--@thymesVar id="data" type="com.ethan.system.pdf.controller.dto.Company"-->


1. maven 添加以下配置
```xml
<!--        解决字体打包遇到的格式不正确问题-->
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
                <excludes>
                    <exclude>static/fonts/**</exclude>
                </excludes>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>false</filtering>
                <includes>
                    <include>static/fonts/**</include>
                </includes>
            </resource>
        </resources>
```
2. 对于i18n 如下配置
```yaml
spring:
  messages:
    basename: i18n/messages
    encoding: UTF-8
    fallback-to-system-locale: true
    cache-duration: 1s
```

3. 解决html i18n国际化 显示为??xxx.xxx??问题
```java
@Configuration
public class LocalConfig {
    @Bean
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }
}

// 解决spring security拦截问题
// 1. @PermitAll
// 2.requestMatchers(HttpMethod.GET, "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/favicon.ico", "/css/**","/img/**","/fonts/**").permitAll()
```
可以使用以下代码手动切换
```java
public class Controller {
    public String showHtml(@PathVariable("tmpName") String tmpName, Model mode, HttpServletRequest request) throws IOException {
        Locale locale = new Locale("zh", "TC");
        new SessionLocaleResolver().setLocale(request, null, locale);
    }
}
```