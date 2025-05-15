-- MySQL dump 10.13  Distrib 8.0.36, for macos14 (arm64)
--
-- Host: localhost    Database: creed-mall-pro
-- ------------------------------------------------------
-- Server version	8.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `creed_system_depts`
--
DROP TABLE IF EXISTS `creed_system_depts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_depts`  (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `name` varchar(30) NOT NULL DEFAULT '' COMMENT '部门名称',
    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父部门id',
    `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `leader_user_id` bigint NULL DEFAULT NULL COMMENT '负责人',
    `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
    `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
    `enabled` tinyint NOT NULL COMMENT '部门状态（0正常 1停用）',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    KEY `CSD_IDX_COMMON` (`name`, `parent_id`),
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 114 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_depts`
--

LOCK TABLES `creed_system_depts` WRITE;
/*!40000 ALTER TABLE `creed_system_depts` DISABLE KEYS */;
INSERT INTO `creed_system_depts` VALUES
    (100,'超级管理部门',0,0,1,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','1','2023-11-14 23:30:36',0,1),
    (101,'深圳总公司',100,1,104,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','1','2023-12-02 09:53:35',0,1),
    (102,'长沙分公司',100,2,NULL,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','','2021-12-15 05:01:40',0,1),
    (103,'研发部门',101,1,104,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','103','2022-01-14 01:04:14',0,1),
    (104,'市场部门',101,2,NULL,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','','2021-12-15 05:01:38',0,1),
    (105,'测试部门',101,3,NULL,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','1','2022-05-16 20:25:15',0,1),
    (106,'财务部门',101,4,103,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','103','2022-01-15 21:32:22',0,1),
    (107,'运维部门',101,5,1,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','1','2023-12-02 09:28:22',0,1),
    (108,'市场部门',102,1,NULL,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','1','2022-02-16 08:35:45',0,1),
    (109,'财务部门',102,2,NULL,'15888888888','xx@gmail.com',0,'admin','2021-01-05 17:03:47','','2021-12-15 05:01:29',0,1),
    (110,'新部门',0,1,NULL,NULL,NULL,0,'110','2022-02-23 20:46:30','110','2022-02-23 20:46:30',0,121),
    (111,'顶级部门',0,1,NULL,NULL,NULL,0,'113','2022-03-07 21:44:50','113','2022-03-07 21:44:50',0,122),
    (112,'产品部门',101,100,1,NULL,NULL,1,'1','2023-12-02 09:45:13','1','2023-12-02 09:45:31',0,1),
    (113,'支持部门',102,3,104,NULL,NULL,1,'1','2023-12-02 09:47:38','1','2023-12-02 09:47:38',0,1);
/*!40000 ALTER TABLE `creed_system_depts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creed_system_posts`
--
DROP TABLE IF EXISTS `creed_system_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_posts`  (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位编码',
    `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '岗位名称',
    `sort` int NOT NULL COMMENT '显示顺序',
    `enabled` tinyint NOT NULL COMMENT '状态（0正常 1停用）',
    `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
    `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
    KEY `CSP_IDX_COMMON` (`code`,`name`),
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_post`
--

LOCK TABLES `creed_system_posts` WRITE;
/*!40000 ALTER TABLE `creed_system_posts` DISABLE KEYS */;
INSERT INTO `creed_system_posts` VALUES
    (1,'ceo','董事长',1,0,'','admin','2021-01-06 17:03:48','1','2023-02-11 15:19:04',0,1),
    (2,'se','项目经理',2,0,'','admin','2021-01-05 17:03:48','1','2023-11-15 09:18:20',0,1),
    (4,'user','普通员工',4,0,'111','admin','2021-01-05 17:03:48','1','2023-12-02 10:04:37',0,1);
/*!40000 ALTER TABLE `creed_system_posts` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-25 11:36:09
--

DROP TABLE IF EXISTS `creed_system_dept_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_dept_users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `dept_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `CSDU_IDX_COMMON` (`dept_id`,`user_id`),
    CONSTRAINT `creed_system_dept_users_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

-- Dumping data for table `creed_system_group_roles`
--

LOCK TABLES `creed_system_dept_users` WRITE;
/*!40000 ALTER TABLE `creed_system_dept_users` DISABLE KEYS */;
INSERT INTO `creed_system_dept_users` VALUES
    (1,100,1,'2024-07-17 11:04:21.490620','default','2024-07-17 11:04:21.490636','default',0),
    (2,100,2,'2024-07-17 11:04:21.494215','default','2024-07-17 11:04:21.494227','default',0),
    (3,100,3,'2024-07-17 11:04:21.497802','default','2024-07-17 11:04:21.497814','default',0);
/*!40000 ALTER TABLE `creed_system_dept_users` ENABLE KEYS */;
UNLOCK TABLES;

DROP TABLE IF EXISTS `creed_system_post_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_post_users` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `post_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `CSGR_IDX_COMMON` (`post_id`,`user_id`),
    CONSTRAINT `creed_system_post_users_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;
--
-- Dumping data for table `creed_system_group_roles`
--

LOCK TABLES `creed_system_post_users` WRITE;
/*!40000 ALTER TABLE `creed_system_post_users` DISABLE KEYS */;
INSERT INTO `creed_system_post_users` VALUES
    (1,1,1,'2024-07-17 11:04:21.490620','default','2024-07-17 11:04:21.490636','default',0),
    (2,1,2,'2024-07-17 11:04:21.494215','default','2024-07-17 11:04:21.494227','default',0),
    (3,1,3,'2024-07-17 11:04:21.497802','default','2024-07-17 11:04:21.497814','default',0);
/*!40000 ALTER TABLE `creed_system_post_users` ENABLE KEYS */;
UNLOCK TABLES;