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
-- Table structure for table `creed_system_authorities`
--

DROP TABLE IF EXISTS `creed_system_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_authorities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `authority` varchar(255) NOT NULL COMMENT '权限类型',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `sort` TINYINT NOT NULL DEFAULT 0,
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '角色类型',
  `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `version` int DEFAULT 0,
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSR_IDX_COMMON` (`authority`),
  CONSTRAINT `creed_system_authorities_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_authorities`
--

LOCK TABLES `creed_system_authorities` WRITE;
/*!40000 ALTER TABLE `creed_system_authorities` DISABLE KEYS */;
INSERT INTO `creed_system_authorities` VALUES
  (1,'system:user:query',NULL,1,1,0,'2024-07-17 11:04:21.256652','default','2024-07-17 11:04:21.256664','default',0,0),
  (2,'system:user:create',NULL,1,1,0,'2024-07-17 11:04:21.260451','default','2024-07-17 11:04:21.260461','default',0,0),
  (3,'system:user:update',NULL,1,1,0,'2024-07-17 11:04:21.266727','default','2024-07-17 11:04:21.266742','default',0,0),
  (4,'system:user:delete',NULL,1,1,0,'2024-07-17 11:04:21.271838','default','2024-07-17 11:04:21.271847','default',0,0),
  (5,'system:user:export',NULL,1,1,0,'2024-07-17 11:04:21.275429','default','2024-07-17 11:04:21.275439','default',0,0),
  (6,'system:role:query',NULL,2,1,0,'2024-07-17 11:04:21.280253','default','2024-07-17 11:04:21.280264','default',0,0),
  (7,'system:role:create',NULL,2,1,0,'2024-07-17 11:04:21.284586','default','2024-07-17 11:04:21.284596','default',0,0),
  (8,'system:role:update',NULL,2,1,0,'2024-07-17 11:04:21.288125','default','2024-07-17 11:04:21.288134','default',0,0),
  (9,'system:role:delete',NULL,2,1,0,'2024-07-17 11:04:21.290566','default','2024-07-17 11:04:21.290576','default',0,0),
  (10,'system:role:export',NULL,2,1,0,'2024-07-17 11:04:21.295571','default','2024-07-17 11:04:21.295581','default',0,0),
  (11,'system:dept:query',NULL,3,2,0,'2024-07-17 11:04:21.301945','default','2024-07-17 11:04:21.301997','default',0,0),
  (12,'system:dept:create',NULL,3,2,0,'2024-07-17 11:04:21.303702','default','2024-07-17 11:04:21.303710','default',0,0),
  (13,'system:dept:update',NULL,3,2,0,'2024-07-17 11:04:21.305787','default','2024-07-17 11:04:21.305804','default',0,0),
  (14,'system:dept:delete',NULL,3,2,0,'2024-07-17 11:04:21.311645','default','2024-07-17 11:04:21.311663','default',0,0),
  (16,'system:menu:query',NULL,3,1,0,'2024-07-18 06:33:48.325856','default','2024-07-18 06:33:48.325953','default',0,0);
/*!40000 ALTER TABLE `creed_system_authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creed_system_groups`
--

DROP TABLE IF EXISTS `creed_system_groups`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_groups` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `group_name` varchar(50) NOT NULL,
    `group_code` varchar(50) NOT NULL,
    `group_non_locked` bit(1) DEFAULT 0,
    `avatar` varchar(512) DEFAULT NULL COMMENT '图像',
    `remark` varchar(255) DEFAULT NULL,
    `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
    `version` int DEFAULT 0,
    `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (`id`),
    KEY `CSG_IDX_COMMON` (`group_name`, `group_code`),
    CONSTRAINT `creed_system_groups_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_groups`
--

LOCK TABLES `creed_system_groups` WRITE;
/*!40000 ALTER TABLE `creed_system_groups` DISABLE KEYS */;
INSERT INTO `creed_system_groups` VALUES
    (1,'超级系统管理员','SYSTEM:ADMIN',0,NULL,NULL,0,'2024-07-17 11:04:21.088926','default','2024-07-17 11:04:21.088990','default',0,0),
    (2,'HR管理员','SYSTEM:HR',0,NULL,NULL,0,'2024-07-17 11:04:21.146340','default','2024-07-17 11:04:21.146354','default',0,0);
/*!40000 ALTER TABLE `creed_system_groups` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creed_system_users`
--

DROP TABLE IF EXISTS `creed_system_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户账号',
  `password` varchar(100) NOT NULL DEFAULT '' COMMENT '密码',
  `nickname` varchar(50) NOT NULL COMMENT '用户昵称',
  `sex` tinyint DEFAULT NULL,
  `avatar` varchar(512) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `country_code` varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
  `phone` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL,
  `acc_non_expired` tinyint DEFAULT 0 COMMENT '账号未过期',
  `acc_non_locked` tinyint DEFAULT 0 COMMENT '账号未锁定',
  `credentials_non_expired` tinyint DEFAULT 0 COMMENT '密码未过期',
  `login_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最后登录时间',
  `login_ip` varchar(100) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `version` int DEFAULT 0,
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `CSU_IDX_COMMON` (`username`,`email`,`phone`),
  CONSTRAINT `creed_system_users_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_users`
--

LOCK TABLES `creed_system_users` WRITE;
/*!40000 ALTER TABLE `creed_system_users` DISABLE KEYS */;
INSERT INTO `creed_system_users` VALUES
 (1,'ethan','{noop}password','super hero',NULL,NULL,NULL,86,NULL,0,0,0,'2024-07-17 11:04:21.167640',NULL,'remarks',0,'2024-07-17 11:04:21.167640','default','2024-07-17 11:04:21.167748','default',0,0,0),
(2,'daming','{noop}password','xiaoming hero',NULL,NULL,NULL,86,NULL,0,0,0,'2024-07-17 11:04:21.179511',NULL,'remarks',0,'2024-07-17 11:04:21.179511','default','2024-07-17 11:04:21.179542','default',0,0,0),
(3,'erming','{noop}password','xiaoming hero',NULL,NULL,NULL,86,NULL,0,0,0,'2024-07-17 11:04:21.184192',NULL,'remarks',0,'2024-07-17 11:04:21.184192','default','2024-07-17 11:04:21.184263','default',0,0,0),
(4,'xiaomi','{noop}password','xiaomi hero',NULL,NULL,NULL,86,NULL,0,0,0,'2024-07-17 11:04:21.190719',NULL,'remarks',0,'2024-07-17 11:04:21.190719','default','2024-07-17 11:04:21.190744','default',0,0,0),
(5,'huawei','{noop}password','huawei hero',NULL,NULL,NULL,86,NULL,0,0,0,'2024-07-17 11:04:21.196767',NULL,'remarks',0,'2024-07-17 11:04:21.196767','default','2024-07-17 11:04:21.196888','default',0,0,0),
(6,'vivo','{noop}password','vivo hero',NULL,NULL,NULL,86,NULL,0,0,0,'2024-07-17 11:04:21.205842',NULL,'remarks',0,'2024-07-17 11:04:21.205842','default','2024-07-17 11:04:21.205999','default',0,0,0),
(7,'oppo','{noop}password','oppo hero',NULL,NULL,NULL,86,NULL,0,0,0,'2024-07-17 11:04:21.210865',NULL,'remarks',0,'2024-07-17 11:04:21.210865','default','2024-07-17 11:04:21.210877','default',0,0,0);
/*!40000 ALTER TABLE `creed_system_users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

--
-- Table structure for table `creed_system_roles`
--

DROP TABLE IF EXISTS `creed_system_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL COMMENT '角色名称',
  `code` varchar(100) NOT NULL COMMENT '角色代码',
  `data_scope` int NOT NULL DEFAULT 1 COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `data_scope_dept_ids` varchar(500) NOT NULL DEFAULT '[]' COMMENT '数据范围(指定部门数组)',
  `remark` varchar(255) DEFAULT NULL,
  `sort` TINYINT NOT NULL DEFAULT 0,
  `type` TINYINT NOT NULL DEFAULT 1 COMMENT '角色类型',
  `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '帐号状态（0正常 1停用）',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `version` int DEFAULT 0,
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSR_IDX_COMMON` (`name`, `code`),
  CONSTRAINT `creed_system_roles_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_roles`
--

LOCK TABLES `creed_system_roles` WRITE;
/*!40000 ALTER TABLE `creed_system_roles` DISABLE KEYS */;
INSERT INTO `creed_system_roles` VALUES
    (1,'超级管理员','super_admin',1,'[]',NULL,1,1,0,'2024-07-17 11:04:21.224452','default','2024-07-17 11:04:21.224511','default',0,0),
    (2,'普通角色','common',1,'[]',NULL,2,1,0,'2024-07-17 11:04:21.231996','default','2024-07-17 11:04:21.232008','default',0,0),
    (3,'HR 部门','hr_common',1,'[]',NULL,3,1,0,'2024-07-17 11:04:21.239415','default','2024-07-17 11:04:21.239426','default',0,0),
    (4,'HR 管理员','hr_admin',1,'[]',NULL,4,2,0,'2024-07-17 11:04:21.248218','default','2024-07-17 11:04:21.248246','default',0,0),
    (5,'CRM 管理员','crm_admin',1,'[]',NULL,4,2,0,'2024-07-17 11:04:21.248218','default','2024-07-17 11:04:21.248246','default',0,0),
    (6,'测试账号','test',1,'[]',NULL,4,2,0,'2024-07-17 11:04:21.248218','default','2024-07-17 11:04:21.248246','default',0,0),
    (7,'租户管理员','tenant_admin',1,'[]',NULL,4,2,0,'2024-07-17 11:04:21.248218','default','2024-07-17 11:04:21.248246','default',0,0);
/*!40000 ALTER TABLE `creed_system_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creed_system_menus`
--

DROP TABLE IF EXISTS `creed_system_menus`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_menus` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `type` tinyint NOT NULL COMMENT '菜单类型',
  `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `path` varchar(200) NULL DEFAULT '' COMMENT '路由地址',
  `icon` varchar(100) NULL DEFAULT '#' COMMENT '菜单图标',
  `component` varchar(255) NULL DEFAULT NULL COMMENT '组件路径',
  `component_name` varchar(255) NULL DEFAULT NULL COMMENT '组件名',
  `visible` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否可见',
  `keep_alive` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否缓存',
  `always_show` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否总是显示',
  `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '菜单状态（0正常 1停用）',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `version` int DEFAULT 0,
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSM_IDX_COMMON` (`name`),
  CONSTRAINT `creed_system_menus_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_menus`
--

LOCK TABLES `creed_system_menus` WRITE;
/*!40000 ALTER TABLE `creed_system_menus` DISABLE KEYS */;
/*!40000 ALTER TABLE `creed_system_menus` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creed_system_group_roles`
--

DROP TABLE IF EXISTS `creed_system_group_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_group_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSGR_IDX_COMMON` (`group_id`,`role_id`),
  CONSTRAINT `creed_system_group_roles_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_group_roles`
--

LOCK TABLES `creed_system_group_roles` WRITE;
/*!40000 ALTER TABLE `creed_system_group_roles` DISABLE KEYS */;
INSERT INTO `creed_system_group_roles` VALUES
    (1,1,1,'2024-07-17 11:04:21.490620','default','2024-07-17 11:04:21.490636','default',0),
    (2,2,3,'2024-07-17 11:04:21.494215','default','2024-07-17 11:04:21.494227','default',0),
    (3,2,4,'2024-07-17 11:04:21.497802','default','2024-07-17 11:04:21.497814','default',0);
/*!40000 ALTER TABLE `creed_system_group_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creed_system_group_users`
--

DROP TABLE IF EXISTS `creed_system_group_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_group_users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `group_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSGU_IDX_COMMON` (`group_id`,`user_id`),
  CONSTRAINT `creed_system_group_users_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_group_users`
--

LOCK TABLES `creed_system_group_users` WRITE;
/*!40000 ALTER TABLE `creed_system_group_users` DISABLE KEYS */;
INSERT INTO `creed_system_group_users` VALUES
    (1,1,1,'2024-07-17 11:04:21.320042','default','2024-07-17 11:04:21.320061','default',0),
    (2,2,2,'2024-07-17 11:04:21.324443','default','2024-07-17 11:04:21.324458','default',0),
    (7,2,3,'2024-07-18 09:40:48.817604','default','2024-07-18 09:40:48.817641','default',0);
/*!40000 ALTER TABLE `creed_system_group_users` ENABLE KEYS */;
UNLOCK TABLES;



--
-- Table structure for table `creed_system_menu_roles`
--

DROP TABLE IF EXISTS `creed_system_menu_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_menu_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `menu_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSMR_IDX_COMMON` (`menu_id`,`role_id`),
  CONSTRAINT `creed_system_menu_roles_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;




--
-- Table structure for table `creed_system_role_authorities`
--

DROP TABLE IF EXISTS `creed_system_role_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_role_authorities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL,
  `authority_id` bigint NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSRA_IDX_COMMON` (`role_id`,`authority_id`),
  CONSTRAINT `creed_system_role_authorities_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=71 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_role_authorities`
--

LOCK TABLES `creed_system_role_authorities` WRITE;
/*!40000 ALTER TABLE `creed_system_role_authorities` DISABLE KEYS */;
INSERT INTO `creed_system_role_authorities` VALUES
    (1,1,1,'2024-07-17 11:04:21.335020','default','2024-07-17 11:04:21.335041','default',0),
    (2,1,2,'2024-07-17 11:04:21.339094','default','2024-07-17 11:04:21.339105','default',0),
    (3,1,3,'2024-07-17 11:04:21.343871','default','2024-07-17 11:04:21.343886','default',0),
    (4,1,4,'2024-07-17 11:04:21.351293','default','2024-07-17 11:04:21.351312','default',0),
    (5,1,5,'2024-07-17 11:04:21.353966','default','2024-07-17 11:04:21.353988','default',0),
    (6,1,6,'2024-07-17 11:04:21.363931','default','2024-07-17 11:04:21.364013','default',0),
    (7,1,7,'2024-07-17 11:04:21.371798','default','2024-07-17 11:04:21.371811','default',0),
    (8,1,8,'2024-07-17 11:04:21.376647','default','2024-07-17 11:04:21.376658','default',0),
    (9,1,9,'2024-07-17 11:04:21.379729','default','2024-07-17 11:04:21.379745','default',0),
    (10,1,10,'2024-07-17 11:04:21.384756','default','2024-07-17 11:04:21.385007','default',0),
    (11,1,11,'2024-07-17 11:04:21.388256','default','2024-07-17 11:04:21.388303','default',0),
    (12,1,12,'2024-07-17 11:04:21.394332','default','2024-07-17 11:04:21.394347','default',0),
    (13,1,13,'2024-07-17 11:04:21.398813','default','2024-07-17 11:04:21.398825','default',0),
    (14,1,14,'2024-07-17 11:04:21.402854','default','2024-07-17 11:04:21.402880','default',0),
    (15,2,1,'2024-07-17 11:04:21.410952','default','2024-07-17 11:04:21.410994','default',0),
    (16,2,2,'2024-07-17 11:04:21.416112','default','2024-07-17 11:04:21.416149','default',0),
    (17,2,6,'2024-07-17 11:04:21.418951','default','2024-07-17 11:04:21.418959','default',0),
    (18,2,7,'2024-07-17 11:04:21.421723','default','2024-07-17 11:04:21.421735','default',0),
    (19,2,11,'2024-07-17 11:04:21.426932','default','2024-07-17 11:04:21.426942','default',0),
    (20,2,12,'2024-07-17 11:04:21.429596','default','2024-07-17 11:04:21.429607','default',0),
    (21,3,1,'2024-07-17 11:04:21.433600','default','2024-07-17 11:04:21.433609','default',0),
    (22,3,2,'2024-07-17 11:04:21.440320','default','2024-07-17 11:04:21.440332','default',0),
    (23,3,3,'2024-07-17 11:04:21.444666','default','2024-07-17 11:04:21.444684','default',0),
    (24,3,4,'2024-07-17 11:04:21.448065','default','2024-07-17 11:04:21.448081','default',0),
    (25,3,5,'2024-07-17 11:04:21.452990','default','2024-07-17 11:04:21.453001','default',0),
    (26,4,6,'2024-07-17 11:04:21.457439','default','2024-07-17 11:04:21.457453','default',0),
    (27,4,7,'2024-07-17 11:04:21.460523','default','2024-07-17 11:04:21.460535','default',0),
    (28,4,8,'2024-07-17 11:04:21.469933','default','2024-07-17 11:04:21.469954','default',0),
    (29,4,9,'2024-07-17 11:04:21.476451','default','2024-07-17 11:04:21.476464','default',0),
    (67,4,11,'2024-07-18 09:40:48.732416','default','2024-07-18 09:40:48.732607','default',0),
    (68,4,12,'2024-07-18 09:40:48.760193','default','2024-07-18 09:40:48.760207','default',0),
    (69,4,13,'2024-07-18 09:40:48.765995','default','2024-07-18 09:40:48.766005','default',0),
    (70,4,14,'2024-07-18 09:40:48.769620','default','2024-07-18 09:40:48.769629','default',0);
/*!40000 ALTER TABLE `creed_system_role_authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creed_system_user_authorities`
--

DROP TABLE IF EXISTS `creed_system_user_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_user_authorities` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `authority_id` bigint NOT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSUA_IDX_COMMON` (`user_id`,`authority_id`),
  CONSTRAINT `creed_system_user_authorities_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_user_authorities`
--

LOCK TABLES `creed_system_user_authorities` WRITE;
/*!40000 ALTER TABLE `creed_system_user_authorities` DISABLE KEYS */;
INSERT INTO `creed_system_user_authorities` VALUES
    (1,7,1,'2024-07-17 11:04:21.518841','default','2024-07-17 11:04:21.518854','default',0),
    (2,7,11,'2024-07-17 11:04:21.523422','default','2024-07-17 11:04:21.523435','default',0),
    (7,3,16,'2024-07-18 09:40:48.839431','default','2024-07-18 09:40:48.839452','default',0);
/*!40000 ALTER TABLE `creed_system_user_authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creed_system_user_roles`
--

DROP TABLE IF EXISTS `creed_system_user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creed_system_user_roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `creator` varchar(64) DEFAULT NULL COMMENT '创建者',
  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `updater` varchar(64) DEFAULT NULL COMMENT '更新者',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `CSUR_IDX_COMMON` (`user_id`,`role_id`),
  CONSTRAINT `creed_system_user_roles_chk_1` CHECK ((`deleted` in (0,1)))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creed_system_user_roles`
--

LOCK TABLES `creed_system_user_roles` WRITE;
/*!40000 ALTER TABLE `creed_system_user_roles` DISABLE KEYS */;
INSERT INTO `creed_system_user_roles` VALUES
    (1,3,4,'2024-07-17 11:04:21.503736','default','2024-07-17 11:04:21.503759','default',0),
    (2,4,5,'2024-07-17 11:04:21.508482','default','2024-07-17 11:04:21.508494','default',0),
    (3,2,6,'2024-07-17 11:04:21.513925','default','2024-07-17 11:04:21.513949','default',0);
/*!40000 ALTER TABLE `creed_system_user_roles` ENABLE KEYS */;
UNLOCK TABLES;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-07-18 18:37:50
