# Junit5 Support
> `spring-boot-starter-test` now provides JUnit 5 by default. JUnit 5’s vintage engine is included by default to support existing JUnit 4-based test classes so that you can migrate to JUnit 5 when you are ready to do so. It is also possible to use a mixture of JUnit 4- and JUnit 5-based test classes in the same module. This allows you to migrate to JUnit 5 gradually if you wish.
>
>https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.2-Release-Notes

此段摘抄至spring官网，但是我在整合使用时，并不是特别顺利，然后无法启用junit5。spring 保留了junit4与junit5，使用Intellij启动时仍然存在问题。

# 整合junit5与spring boot
junit5出现已经过去很久了，然而许多项目并没有对此升级到junit5，技术总是会升级的，因为本人尝试开始学习junit5。
使用的spring boot 版本是`2.1.9.RELEASE`

因为spring boot 任然保留了junit4的兼容，所以在写testCase的时候很容易一不小心就导入了`org.junit.Test` junit4的包。

经过多次排坑探索发现，需要exclude junit4的包，引入`junit-platform-launcher`，这样IntelliJ IDEA 才能正确的执行junit5的配置。具体maven配置如下：
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <exclusions>
        <exclusion>
            <!-- exclude Junit 4 before spring boot 2.2.x -->
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </exclusion>
        <!-- exclude Junit 5 before spring boot 2.2.x -->
        <!--<exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
        </exclusion>-->
    </exclusions>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>${junit.version}</version>
    <scope>test</scope>
</dependency>

<!-- Only needed to run tests in a version of IntelliJ IDEA that bundles older versions -->
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-launcher</artifactId>
    <scope>test</scope>
    <version>${junit.platform.version}</version>
</dependency>
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-commons</artifactId>
    <scope>test</scope>
    <version>${junit.platform.version}</version>
</dependency>
```
