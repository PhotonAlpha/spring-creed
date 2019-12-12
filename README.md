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

# https://zhuanlan.zhihu.com/p/86267058

# sample https://github.com/wyh-spring-ecosystem-student/spring-boot-student/tree/releases
# redis & caffeine cache https://www.jianshu.com/p/ef9042c068fd

# redis cache解释: https://my.oschina.net/damonchow/blog/3011422



# Junit5 Support
spring-boot-starter-test >2.2.0 comes with Junit 5, so no need for this if you use the most recent version of Spring Boot (or of spring-boot-starter-web).

# 基于Redis的分布式锁到底安全吗?
https://www.jianshu.com/p/dd66bdd18a56
https://martin.kleppmann.com/2016/02/08/how-to-do-distributed-locking.html
https://redis.io/topics/distlock
 - 待优化: https://blog.piaoruiqing.com/2019/05/19/redis%E5%88%86%E5%B8%83%E5%BC%8F%E9%94%81/