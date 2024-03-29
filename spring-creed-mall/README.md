# Nacos配置中心
1. 命名空间: 配置隔离
   1. 默认:public(保留空间);默认新增的所有配置都在public空间
   2. 利用命名空间,来做环境隔离;需要在`bootstrap.properties`中配置`spring.cloud.nacos.config.namespace=c625682c-bc1b-4351-9e24-d13ecb3803a7`
   3. 基于每个微服务之间互相隔离配置,每一个微服务都创建自己的命名空间,只加载自己命名空间下的所有配置 `coupon`
2. 配置集: 所有的配置的集合
3. 配置集ID: 类似于文件名.`Data Id`
4. **配置分组**:
   1. 默认所有的配置集都属于:DEFAULT_GROUP
   2. 可以自定义分组,并且配置 `spring.cloud.nacos.config.group=1111`
5. 同时加载多个配置集
   ```yaml
   spring:
      application:
         name: creedmall-coupon
      cloud:
         nacos:
            config:
               server-addr: hadoop100:8848
               namespace: ac2b161f-ab07-4db7-b36a-d173bf7ff273
               group: dev
               extension-configs:
                  - data-id: datasource.yml
                    group: dev
                    refresh: true
                  - data-id: mybatis.yml
                    group: dev
                    refresh: true
                  - data-id: datasource.yml
                    group: dev
                    refresh: true   
   ```


##### 本项目命名规则
- 每个微服务创建自己的命名空间,使用配置分组区分环境, dev, sit, prod

# Gateway网关

# 文件上传 OSS


creed-mall-third-party


# SPU 与 SKU
### SPU： Standard Product Unit(标准化产品单元)
指商品信息聚合的最小单元，是一组可复合、易检索的标准化信息的集合，**该集合描述了一个产品的特性**。
### SKU： Stock Keeping Unit(库存量单元)
指库存进出计量的基本单元，可以以件、盒、托盘等为单位，现在已经被引申为产品统一编号的简称，每种产品均对应有唯一的SKU号。
