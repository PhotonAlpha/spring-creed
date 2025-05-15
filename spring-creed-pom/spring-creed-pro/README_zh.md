## About Me

这是一个前端基于VUE3 + element plus, 后端基于Spring boot 和 Spring cloud 快速开发平台。主要目的是用来学习Spring 最新的技术并且用在实践中。

该项目主要包含后台管理功能（商城管理系统）和个人博客功能。

系统内置多种多种业务模块，可以用于快速你的业务系统：

* 通用模块（必选）：系统功能、基础设施
* 通用模块（可选）：工作流程、支付系统、数据报表、会员中心
* 业务系统（TODO）：ERP 系统、CRM 系统、商城系统、微信公众号、AI 大模型

[博客网站](https://photonalpha.github.io/springboot)

### 系统功能

|      | 功能       | 描述                                                         |
| ---- | ---------- | ------------------------------------------------------------ |
| 👩‍💻   | 用户管理   | 用户是系统操作者，该功能主要完成系统用户配置                 |
| ⭐️    | 在线用户   | 当前系统中活跃用户状态监控，支持手动踢下线                   |
| 👫🏻   | 角色管理   | 角色菜单权限分配、设置角色按机构进行数据范围权限划分         |
| 🍽️    | 菜单管理   | 配置系统菜单、操作权限、按钮权限标识等，本地缓存提供性能     |
| 👥    | 部门管理   | 配置系统组织机构（公司、部门、小组），树结构展现支持数据权限 |
| ⭐️    | 岗位管理   | 配置系统用户所属担任职务                                     |
| ⭐️    | 字典管理   | 对系统中经常使用的一些较为固定的数据进行维护                 |
| ⭐️    | 短信管理   | 短信渠道、短息模板、短信日志，对接阿里云、腾讯云等主流短信平台 |
| ⭐️    | 邮件管理   | 邮箱账号、邮件模版、邮件发送日志，支持所有邮件平台           |
| ⭐️    | 站内信     | 系统内的消息通知，提供站内信模版、站内信消息                 |
| ⭐️    | 操作日志   | 系统正常操作日志记录和查询，集成 Swagger 生成日志内容        |
| ⭐️    | 登录日志   | 系统登录日志记录查询，包含登录异常                           |
| 🚧    | 错误码管理 | 系统所有错误码的管理，可在线修改错误提示，无需重启服务       |
| 🚧    | 通知公告   | 系统通知公告信息发布维护                                     |
| 🚧    | 敏感词     | 配置系统敏感词，支持标签分组                                 |
| ⭐️    | 应用管理   | 管理 SSO 单点登录的应用，支持多种 OAuth2 授权方式            |
| 🚧    | 地区管理   | 展示省份、城市、区镇等城市信息，支持 IP 对应城市             |



### 框架

| 框架                                                         | 说明                 | 版本           | 学习指南 |
| ------------------------------------------------------------ | -------------------- | -------------- | -------- |
| [Spring Boot](https://spring.io/projects/spring-boot)        | 应用开发框架         | 3.3.0          | -        |
| [MySQL](https://www.mysql.com/cn/)                           | 数据库服务器         | 5.7 / 8.0+     | -        |
| [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc) | ORM框架              | 3.3.0          | -        |
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | ORM框架              | 3.3.0          | -        |
| [Redis](https://redis.io/)                                   | key-value 数据库     | 5.0 / 6.0 /7.0 | -        |
| [Redisson](https://github.com/redisson/redisson)             | Redis 客户端         | 3.32.0         | -        |
| [Spring MVC](https://github.com/spring-projects/spring-framework/tree/master/spring-webmvc) | MVC 框架             | 6.1.10         | -        |
| [Spring Security](https://github.com/spring-projects/spring-security) | Spring 安全框架      | 6.3.1          | -        |
| [JSON Schema Validator](https://github.com/networknt/json-schema-validator) | 参数校验组件         | 8.0.1          | -        |
| [Flowable](https://github.com/flowable/flowable-engine)      | 工作流引擎           | 7.0.0          | -        |
| [Quartz](https://github.com/quartz-scheduler)                | 任务调度组件         | 2.3.2          | -        |
| [Springdoc](https://springdoc.org/)                          | Swagger 文档         | 2.3.0          | -        |
| [SkyWalking](https://skywalking.apache.org/)                 | 分布式应用追踪系统   | 9.0.0          | -        |
| [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) | Spring Boot 监控平台 | 3.3.2          | -        |
| [Jackson](https://github.com/FasterXML/jackson)              | JSON 工具库          | 2.17.1         | -        |
| [MapStruct](https://mapstruct.org/)                          | Java Bean 转换       | 1.5.3.Final    | -        |
| [Lombok](https://projectlombok.org/)                         | 消除冗长的 Java 代码 | 1.18.34        | -        |
| [JUnit](https://junit.org/junit5/)                           | Java 单元测试框架    | 5.10.2         | -        |
| [Mockito](https://github.com/mockito/mockito)                | Java Mock 框架       | 5.11.0         | -        |



### 技术解决方案：

- [x] 分布式日志链路

- [ ] 消息幂等性
- [ ] spring boot reactive改造
- [x] 定时任务
- [ ] 布隆过滤器
- [ ] 分布式事务
- [ ] 读写分离
- [ ] 压力测试



参考文章：

- [Java 新特性](https://www.wdbyte.com/2020/02/jdk/jdk12-feature/#%E8%AE%A2%E9%98%85)

- [分布式文章](https://pdai.tech/md/arch/arch-z-job.html)
- [sas安全认证](https://wukong-doc.redhtc.com/security/sas/sas-whatyouknow/)
- [什么是 ID Token](https://docs.authing.cn/v2/concepts/id-token.html)
- [云设计模式](https://iambowen.gitbooks.io/cloud-design-pattern/content/cloud-design-patterns.html)
- [system-design 文章](https://javaguide.cn/system-design/security/data-desensitization.html)

