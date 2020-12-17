多数据源的方案，对于大多数场景下，【基于 SpringAbstractRoutingDataSource 做拓展】，基本能够满足。这种方案，目前是比较主流的方案，大多数项目都采用。在实现上，我们可以比较容易的自己封装一套，当然也可以考虑使用 dynamic-datasource-spring-boot-starter 开源项目。不过呢，建议可以把它的源码撸一下，核心代码估计 1000 行左右。


JTA Hikari的一些研究
https://github.com/brettwooldridge/HikariCP/issues/1013

分布式事务的研究
http://www.iocoder.cn/Sharding-Sphere/Distributed-transactions-are-implemented-in-Sharding-Sphere/

Sharding-JDBC 多数据源，读写分离
http://www.iocoder.cn/Spring-Boot/dynamic-datasource/?self

---------

# 施工中。。。。。。。。

---------
