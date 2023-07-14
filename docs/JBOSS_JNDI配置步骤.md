# JNDI with jboss
## 1. 修改 `standalone.xml`
```html
    <subsystem xmlns="urn:jboss:domain:datasources:5.0">
      <datasources>
          <!--            这里是必须要的-->
        <datasource jndi-name="java:jboss/ExampleDS" pool-name="ExampleDS" enabled="true" use-java-context="true">
          <connection-url>jdbc:mariadb://localhost:3306/example</connection-url>
          <driver-class>org.mariadb.jdbc.Driver</driver-class>
          <driver>mariadb</driver>
          <pool>
            <min-pool-size>10</min-pool-size>
            <initial-pool-size>5</initial-pool-size>
            <max-pool-size>200</max-pool-size>
            <prefill>true</prefill>
            <flush-strategy>IdleConnections</flush-strategy>
          </pool>
          <security>
            <user-name>root</user-name>
          </security>
          <validation>
            <valid-connection-checker class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker" />
            <check-valid-connection-sql>SELECT 1</check-valid-connection-sql>
            <validate-on-match>false</validate-on-match>
            <background-validation>false</background-validation>
            <exception-sorter class-name="org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter" />
          </validation>
          <timeout>
            <set-tx-query-timeout>false</set-tx-query-timeout>
            <blocking-timeout-millis>30</blocking-timeout-millis>
            <idle-timeout-minutes>3</idle-timeout-minutes>
            <query-timeout>0</query-timeout>
            <allocation-retry>1</allocation-retry>
            <allocation-retry-wait-millis>0</allocation-retry-wait-millis>
          </timeout>
          <statement>
            <share-prepared-statements>false</share-prepared-statements>
          </statement>
        </datasource>
        <drivers>
<!--            这里是必须要的-->
          <driver name="mariadb" module="org.mariadb">
            <xa-datasource-class>org.mariadb.jdbc.MySQLDataSource</xa-datasource-class>
          </driver>
        </drivers>
      </datasources>
    </subsystem>
```

## 2. 添加mariaDB Driver作为jboss依赖
    
- cd ${JBOSS_HOME}\modules\system\layers\base
- 创建 org\mariadb\main 文件夹
- 添加 mariadb-java-client-x.x.x.jar driver
- 创建 `module.xml`. **注意** `name="org.mariadb"` 需要与`module.xml`中的路径相同。
   
   ```html
    <module xmlns="urn:jboss:module:1.5" name="org.mariadb">
        <properties>
          <property name="jboss.api" value="unsupported"/>
        </properties>
        <resources>
            <resource-root path="mariadb-java-client-x.x.x.jar"/>
        </resources>
        <dependencies>
            <module name="javax.api"/>
            <module name="javax.transaction.api"/>
            <module name="javax.servlet.api" optional="true"/>
        </dependencies>
    </module>
    ```

## 3. 启动jboss    `standalone.bat` 
启动admin console 检查JNDI是否已经启动
Configuration -> Subsystems -> Datasources & Drivers -> 查看 Datasources/JDBC Drivers [ExampleDS]是否是绿色。如果是就可以部署你的war
```yaml
spring:
  datasource:
    jndi-name: java:jboss/ExampleDS
```