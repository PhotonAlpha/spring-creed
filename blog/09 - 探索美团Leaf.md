# 探索美团Leaf

## Leaf Segment

Leaf第一个版本采用了预分发的方式生成ID，即可以在DB之上挂N个Server，每个Server启动时，都会去DB拿固定长度的ID List。这样就做到了完全基于分布式的架构，同时因为ID是由内存分发，所以也可以做到很高效。接下来是数据持久化问题，Leaf每次去DB拿固定长度的ID List，然后把最大的ID持久化下来，也就是并非每个ID都做持久化，仅仅持久化一批ID中最大的那一个。这个方式有点像游戏里的定期存档功能，只不过存档的是未来某个时间下发给用户的ID，这样极大地减轻了DB持久化的压力。

- Leaf Server 1：从DB加载号段[1，1000]。
- Leaf Server 2：从DB加载号段[1001，2000]。
- Leaf Server 3：从DB加载号段[2001，3000]。

用户通过Round-robin的方式调用Leaf Server的各个服务，所以某一个Client获取到的ID序列可能是：1，1001，2001，2，1002，2002……也可能是：1，2，1001，2001，2002，2003，3，4……当某个Leaf Server号段用完之后，下一次请求就会从DB中加载新的号段，这样保证了每次加载的号段是递增的。

Segment 需要依赖数据库，在启动之前需要在数据库建标.

```sql
DROP TABLE IF EXISTS `creed_system_leaf_alloc`;

CREATE TABLE `creed_system_leaf_alloc` (
    `biz_tag` varchar(128)  NOT NULL DEFAULT '',
    `max_id` bigint(20) NOT NULL DEFAULT '1',
    `step` int(11) NOT NULL,
    `description` varchar(256)  DEFAULT NULL,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB;

INSERT INTO `creed_system_leaf_alloc` (biz_tag, max_id, step, description, update_time) VALUES ('test1', 81, 5, 'myTest', '2024-12-27 19:04:40');
INSERT INTO `creed_system_leaf_alloc` (biz_tag, max_id, step, description, update_time) VALUES ('test2', 1, 5, 'myTest', '2024-12-27 18:03:59');

```



## Leaf Snowflake

Snowflake，Twitter开源的一种分布式ID生成算法。基于64位数实现，下图为Snowflake算法的ID构成图。

- 第1位置为0。
- 第2-42位是相对时间戳，通过当前时间戳减去一个固定的历史时间戳生成。
- 第43-52位是机器号workerID，每个Server的机器ID不同。
- 第53-64位是自增ID。

这样通过时间+机器号+自增ID的组合来实现了完全分布式的ID下发。

在这里，Leaf提供了Java版本的实现，同时对Zookeeper生成机器号做了弱依赖处理，即使Zookeeper有问题，也不会影响服务。Leaf在第一次从Zookeeper拿取workerID后，会在本机文件系统上缓存一个workerID文件。即使ZooKeeper出现问题，同时恰好机器也在重启，也能保证服务的正常运行。这样做到了对第三方组件的弱依赖，一定程度上提高了SLA。

## 内置api列表

```tex
#API
- http://localhost:48080/leaf/segment/creed-mall
- http://localhost:48080/leaf/segment/creed-mall
# UI
- http://localhost:48080/leaf/db
- http://localhost:48080/leaf/cache
- http://localhost:48080/leaf/add-biz-tag?bizTag=creed-mall&maxId=1&description=myDescription&step=10
- http://localhost:48080/leaf/remove-biz-tag?bizTag=creed-mall2
```

[参考文章](https://tech.meituan.com/2019/03/07/open-source-project-leaf.html)
