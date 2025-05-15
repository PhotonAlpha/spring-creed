## About Me

This is a rapid development platform based on VUE3 + element plus on the front end and Spring boot and Spring cloud on the back end. The main purpose of this project is to learn the latest Spring technology and use it in action.

The project mainly includes backend data management (**mall management system**) and personal blog website.

The system has built-in various business modules, which can be used to quickly improve your business system:

* Common modules (mandatory): system functions, security solutions, infrastructure
* Common modules (optional): workflow, payment system, data report, member center
* Business system (TODO): ERP system, CRM system, shopping mall system, AI big model

[Blog Site](https://photonalpha.github.io/springboot)

### System Features

|      | Function               | Describe                                                     |
| ---- | ---------------------- | ------------------------------------------------------------ |
| 👩‍💻   | User Management        | The user is the system operator. This function mainly completes the system user configuration. |
| ⭐️    | Online users           | Active user status monitoring in the current system, support for manual offline |
| 👫🏻   | Role Management        | Role menu permission allocation, set roles and divide data scope permissions by organization |
| 🍽️    | Menu Management        | Configure system menus, operation permissions, button permission identifiers, etc. Local cache provides performance |
| 👥    | Department Management  | Configure system organization (company, department, group), tree structure display supports data permissions |
| ⭐️    | Position Management    | Configure the system user's position                         |
| ⭐️    | Dictionary Management  | Maintain some relatively fixed data that is frequently used in the system |
| ⭐️    | SMS management         | SMS channels, SMS templates, SMS logs, and integration with mainstream SMS platforms such as Alibaba Cloud and Tencent Cloud |
| ⭐️    | Email Management       | Email account, email template, email sending log, support all email platforms |
| ⭐️    | Site Message           | Message notifications within the system, providing in-station message templates and in-station message messages |
| ⭐️    | Operation log          | System normal operation log recording and query, integrated Swagger to generate log content |
| ⭐️    | Login Log              | System login log record query, including login exceptions    |
| 🚧    | Error code management  | Management of all system error codes, online modification of error prompts without restarting the service |
| 🚧    | Notice Announcement    | System notification announcement information release maintenance |
| 🚧    | Sensitive words        | Configure system sensitive words and support tag grouping    |
| ⭐️    | Application Management | Manage SSO single sign-on applications and support multiple OAuth2 authorization methods |
| 🚧    | Regional Management    | Displays information about provinces, cities, districts, towns, etc., and supports IP-based cities |



### Framework

| Framework                                                    | Description                            | Version        | Guidebook |
| ------------------------------------------------------------ | -------------------------------------- | -------------- | --------- |
| [Spring Boot](https://spring.io/projects/spring-boot)        | Application Development Framework      | 3.3.0          | -         |
| [MySQL](https://www.mysql.com/cn/)                           | Database Server                        | 5.7 / 8.0+     | -         |
| [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc) | ORM Framework                          | 3.3.0          | -         |
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | ORM Framework                          | 3.3.0          | -         |
| [Redis](https://redis.io/)                                   | key-value Database                     | 5.0 / 6.0 /7.0 | -         |
| [Redisson](https://github.com/redisson/redisson)             | Redis Client                           | 3.32.0         | -         |
| [Spring MVC](https://github.com/spring-projects/spring-framework/tree/master/spring-webmvc) | MVC Framework                          | 6.1.10         | -         |
| [Spring Security](https://github.com/spring-projects/spring-security) + [Spring Authorization Server](https://spring.io/projects/spring-authorization-server) | Spring Security Framework              | 6.3.1          | -         |
| [JSON Schema Validator](https://github.com/networknt/json-schema-validator) | Parameter verification component       | 8.0.1          | -         |
| [Flowable](https://github.com/flowable/flowable-engine)      | Workflow Engine                        | 7.0.0          | -         |
| [Quartz](https://github.com/quartz-scheduler)                | Task Scheduling Component              | 2.3.2          | -         |
| [Springdoc](https://springdoc.org/)                          | Swagger documentation                  | 2.3.0          | -         |
| [SkyWalking](https://skywalking.apache.org/)                 | Distributed Application Tracing System | 9.0.0          | -         |
| [Spring Boot Admin](https://github.com/codecentric/spring-boot-admin) | Spring Boot Monitoring Platform        | 3.3.2          | -         |
| [Jackson](https://github.com/FasterXML/jackson)              | JSON Tool Library                      | 2.17.1         | -         |
| [MapStruct](https://mapstruct.org/)                          | Java Bean Conversion                   | 1.5.3.Final    | -         |
| [Lombok](https://projectlombok.org/)                         | Eliminate verbose Java code            | 1.18.34        | -         |
| [JUnit](https://junit.org/junit5/)                           | Java Unit Testing Framework            | 5.10.2         | -         |
| [Mockito](https://github.com/mockito/mockito)                | Java Mock Framework                    | 5.11.0         | -         |

### Technical solution:：

- [x] Distributed log tracing

- [ ] Message idempotence
- [ ] spring reactive + spring web flux
- [x] Scheduled tasks
- [ ] Bloom filter for cache
- [ ] Distributed Transactions
- [ ] Read-write separation
- [ ] Stress Testing



References：

- [Java 新特性](https://www.wdbyte.com/2020/02/jdk/jdk12-feature/#%E8%AE%A2%E9%98%85)

- [分布式文章](https://pdai.tech/md/arch/arch-z-job.html)
- [sas安全认证](https://wukong-doc.redhtc.com/security/sas/sas-whatyouknow/)
- [什么是 ID Token](https://docs.authing.cn/v2/concepts/id-token.html)
- [云设计模式](https://iambowen.gitbooks.io/cloud-design-pattern/content/cloud-design-patterns.html)
- [system-design 文章](https://javaguide.cn/system-design/security/data-desensitization.html)

