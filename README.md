spring-creed
- spring-creed-app `main-entry`
- spring-creed-core `core configuration`
- spring-creed-common `common configuration (e.g. swagger)`
- spring-creed-cloud `cloud configuration`
- spring-creed-cache `cache configuration`
- spring-creed-component `include bussiness component`

Redis distributed lock Ref: 
1. https://juejin.im/post/5c8cb043e51d4528641bdefb
2. http://www.programmersought.com/article/459034549/


# blog design: https://zhangjia.io/852.html
# cache: https://github.com/xiaolyuh/layering-cache
# redis:
- https://zhuanlan.zhihu.com/p/86267058
- https://github.com/wyh-spring-ecosystem-student/spring-boot-student/tree/releases
- https://www.jianshu.com/p/ef9042c068fd

# redis cache解释: https://my.oschina.net/damonchow/blog/3011422



# Junit5 Support
spring-boot-starter-test >2.2.0 comes with Junit 5, so no need for this if you use the most recent version of Spring Boot (or of spring-boot-starter-web).

# 基于Redis的分布式锁到底安全吗?
https://www.jianshu.com/p/dd66bdd18a56
https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html
https://redis.io/topics/distlock
 - 待优化: https://blog.piaoruiqing.com/2019/05/19/redis%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81/
 
 
# 理解OAuth 2.0
https://www.ruanyifeng.com/blog/2014/05/oauth_2_0.html  
article:  
- https://blog.csdn.net/u010475041/article/details/78484854
- https://zhuanlan.zhihu.com/p/77323234
- **https://www.cnblogs.com/xiaofengxzzf/p/10733955.html**  https://github.com/githubzengzhifeng/springboot-security-oauth2

# 终极指南
http://www.iocoder.cn/Spring-Security/OAuth2-learning/

# spring security 源码分析
http://www.iocoder.cn/Spring-Security/longfei/The-authorization-process/
http://www.iocoder.cn/Spring-Security/good-collection/

---------------------------------------------------------

# springboot 2.0使用spring-security-oauth2的迁移指南

有朋友使用了 springboot2.0 之后发现原来的demo不能用了，我调试了下，发现springboot2.0和spring5的改动还是挺多的，帮大家踩下坑
改动一 暴露AuthenticationManager

springboot2.0 的自动配置发生略微的变更，原先的自动配置现在需要通过@Bean暴露，否则你会得到AuthenticationManager找不到的异常

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide 查看 Security 相关的改动
改动二 添加PasswordEncoder

如果你得到这个异常 java.lang.IllegalArgumentException: There is no PasswordEncoder mapped for the id "null" springboot2.0 需要增加一个加密器，原来的 plainTextPasswordEncoder 新版本被移除
方法一

    @Bean
    PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }

如上是最简单的修改方式，但缺点很明显，是使用明文存储的密码
方法二

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    
    
    String finalPassword = new BCryptPasswordEncoder().encode("123456");
    String finalSecret = new BCryptPasswordEncoder().encode("123456");

配置具体的 BCryptPasswordEncoder，别忘了存储的密码和oauth client 的 secret 也要存储对应的编码过后的密码，而不是明文！
方法三

    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    String finalPassword = "{bcrypt}"+new BCryptPasswordEncoder().encode("123456");
    String finalSecret = "{bcrypt}"+new BCryptPasswordEncoder().encode("123456");

使用 PasswordEncoderFactories.createDelegatingPasswordEncoder() 创建一个 DelegatingPasswordEncoder，这个委托者通过密码的前缀来 区分应该使用哪一种编码器去校验用户登录时的密码，文档中推荐使用这种方式，并且推荐使用 Bcrypt 而不是 md5,sha256 之类的算法，具体原因看文档

了解细节戳这里：https://docs.spring.io/spring-security/site/docs/5.0.4.RELEASE/reference/htmlsingle/#10.3.2 DelegatingPasswordEncoder





https://github.com/EYHN/hexo-helper-live2d/blob/master/README.zh-CN.md