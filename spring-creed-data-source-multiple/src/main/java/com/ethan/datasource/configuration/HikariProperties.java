package com.ethan.datasource.configuration;

public class HikariProperties {
  //此属性控制从池返回的连接的默认自动提交行为。它是一个布尔值。 默认值：true
  private Boolean autoCommit;
  //此属性表示连接池的用户定义名称，主要出现在日志记录和JMX管理控制台中以识别池和池配置。 默认：自动生成
  private String poolName;

  //此属性控制客户端（即您）将等待来自池的连接的最大毫秒数。
  // 如果在没有可用连接的情况下超过此时间，则会抛出SQLException。最低可接受的连接超时时间为250 ms。 默认值：30000（30秒）
  private String connectionTimeout = "30000";
  //此属性控制允许连接在池中闲置的最长时间。
  // 此设置仅适用于minimumIdle定义为小于maximumPoolSize。
  // 一旦池达到连接，空闲连接将不会退出minimumIdle。
  // 连接是否因闲置而退出，最大变化量为+30秒，平均变化量为+15秒。
  // 在超时之前，连接永远不会退出。
  // 值为0意味着空闲连接永远不会从池中删除。允许的最小值是10000ms（10秒）。 默认值：600000（10分钟）
  private String idleTimeout = "60000";
  //此属性控制池中连接的最大生存期。
  // 正在使用的连接永远不会退休，只有在关闭后才会被删除。
  // 在逐个连接的基础上，应用较小的负面衰减来避免池中的大量消失。
  // 我们强烈建议设置此值，并且应该比任何数据库或基础设施规定的连接时间限制短几秒。
  // 值为0表示没有最大寿命（无限寿命），当然是idleTimeout设定的主题。 默认值：1800000（30分钟）
  private String maxLifetime = "600000";
  //该属性控制HikariCP尝试在池中维护的最小空闲连接数。
  // 如果空闲连接低于此值并且连接池中的总连接数少于此值maximumPoolSize，
  // 则HikariCP将尽最大努力快速高效地添加其他连接。但是，为了获得最佳性能和响应尖峰需求，
  // 我们建议不要设置此值，而是允许HikariCP充当固定大小的连接池。 默认值：与maximumPoolSize相同
  private String minimumIdle = "10";
  //此属性控制池允许达到的最大大小，包括空闲和正在使用的连接。
  // 基本上这个值将决定到数据库后端的最大实际连接数。
  // 对此的合理值最好由您的执行环境决定。
  // 当池达到此大小并且没有空闲连接可用时，对getConnection（）的调用将connectionTimeout在超时前阻塞达几毫秒。
  // 请阅读关于池尺寸。 默认值：10
  private String maximumPoolSize = "10"; // 65
  //此属性控制在记录消息之前连接可能离开池的时间量，表明可能存在连接泄漏。
  // 值为0意味着泄漏检测被禁用。启用泄漏检测的最低可接受值为2000（2秒）。 默认值：0
  private String leakDetectionThreshold = "5000";
}
