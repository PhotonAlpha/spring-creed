-- MySQL dump 10.13  Distrib 8.0.36, for macos14 (arm64)
--
-- Host: localhost    Database: ruoyi-vue-pro
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
-- Table structure for table `system_dept`
--

DROP TABLE IF EXISTS `system_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_dept`
(
    `id`             bigint   NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `name`           varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `parent_id`      bigint   NOT NULL                       DEFAULT '0' COMMENT '父部门id',
    `sort`           int      NOT NULL                       DEFAULT '0' COMMENT '显示顺序',
    `leader_user_id` bigint                                  DEFAULT NULL COMMENT '负责人',
    `phone`          varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `email`          varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `status`         int                                     DEFAULT NULL,
    `creator`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `create_time`    datetime NOT NULL                       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`        varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `update_time`    datetime NOT NULL                       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`        int                                     DEFAULT '0',
    `tenant_id`      bigint   NOT NULL                       DEFAULT '0' COMMENT '租户编号',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_dept`
--

LOCK
TABLES `system_dept` WRITE;
/*!40000 ALTER TABLE `system_dept` DISABLE KEYS */;
INSERT INTO `system_dept`
VALUES (100, '芋道源码', 0, 0, 1, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '1',
        '2023-11-14 23:30:36', 0, 1),
       (101, '深圳总公司', 100, 1, 104, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '1',
        '2023-12-02 09:53:35', 0, 1),
       (102, '长沙分公司', 100, 2, NULL, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '',
        '2021-12-15 05:01:40', 0, 1),
       (103, '研发部门', 101, 1, 104, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '103',
        '2022-01-14 01:04:14', 0, 1),
       (104, '市场部门', 101, 2, NULL, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '',
        '2021-12-15 05:01:38', 0, 1),
       (105, '测试部门', 101, 3, NULL, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '1',
        '2022-05-16 20:25:15', 0, 1),
       (106, '财务部门', 101, 4, 103, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '103',
        '2022-01-15 21:32:22', 0, 1),
       (107, '运维部门', 101, 5, 1, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '1',
        '2023-12-02 09:28:22', 0, 1),
       (108, '市场部门', 102, 1, NULL, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '1',
        '2022-02-16 08:35:45', 0, 1),
       (109, '财务部门', 102, 2, NULL, '15888888888', 'ry@qq.com', 0, 'admin', '2021-01-05 17:03:47', '',
        '2021-12-15 05:01:29', 0, 1),
       (110, '新部门', 0, 1, NULL, NULL, NULL, 0, '110', '2022-02-23 20:46:30', '110', '2022-02-23 20:46:30', 0, 121),
       (111, '顶级部门', 0, 1, NULL, NULL, NULL, 0, '113', '2022-03-07 21:44:50', '113', '2022-03-07 21:44:50', 0, 122),
       (112, '产品部门', 101, 100, 1, NULL, NULL, 1, '1', '2023-12-02 09:45:13', '1', '2023-12-02 09:45:31', 0, 1),
       (113, '支持部门', 102, 3, 104, NULL, NULL, 1, '1', '2023-12-02 09:47:38', '1', '2023-12-02 09:47:38', 0, 1),
       (114, 'dsa', 115, 1, 1, '13013780929', 'dsa@das.dsa', 0, '1', '2024-09-04 19:30:53', '1', '2024-09-04 19:35:24',
        0, 1),
       (115, '测试', 104, 1, 1, NULL, NULL, 0, '1', '2024-09-04 19:32:34', '1', '2024-09-04 19:34:48', 0, 1),
       (116, '测试公司', 100, 1, 1, NULL, NULL, 0, '1', '2024-09-04 19:33:32', '1', '2024-09-04 19:34:48', 0, 1),
       (117, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, '2024-09-04 19:34:48', NULL, '2024-09-04 19:34:48', 0, 0),
       (118, '的份', 115, 1, 103, NULL, NULL, 0, '1', '2024-09-04 19:37:04', '1', '2024-09-04 19:37:21', 0, 1),
       (119, 'QUA4', 100, 0, 129, NULL, NULL, 0, '1', '2024-09-04 19:38:40', '1', '2024-09-04 19:51:39', 0, 1),
       (120, 'NXG', 119, 0, NULL, NULL, NULL, 0, '1', '2024-09-04 19:40:12', '1', '2024-09-04 19:50:10', 1, 1),
       (121, 'QUA1004', 119, 1, NULL, NULL, NULL, 0, '1', '2024-09-04 19:40:36', '1', '2024-09-04 19:40:36', 0, 1),
       (122, 'ADC', 119, 0, NULL, NULL, NULL, 0, '1', '2024-09-04 19:41:10', '1', '2024-09-04 19:50:13', 1, 1),
       (123, 'TDC', 119, 0, NULL, NULL, NULL, 0, '1', '2024-09-04 19:41:21', '1', '2024-09-04 19:50:16', 1, 1),
       (124, 'PDN', 100, 0, NULL, NULL, NULL, 0, '1', '2024-09-04 19:44:33', '1', '2024-09-04 19:44:33', 0, 1),
       (125, 'PDN0041', 124, 0, 129, NULL, NULL, 0, '1', '2024-09-04 19:44:49', '1', '2024-09-04 19:51:48', 0, 1),
       (126, 'ADC', 125, 0, NULL, NULL, NULL, 0, '1', '2024-09-04 19:48:30', '1', '2024-09-04 19:52:06', 0, 1),
       (127, 'TDC', 125, 1, NULL, NULL, NULL, 0, '1', '2024-09-04 19:48:44', '1', '2024-09-04 19:52:14', 0, 1),
       (128, 'ADC', 121, 0, NULL, NULL, NULL, 0, '1', '2024-09-04 19:56:18', '1', '2024-09-04 19:56:18', 0, 1),
       (129, 'TDC', 121, 0, NULL, NULL, NULL, 0, '1', '2024-09-04 19:56:35', '1', '2024-09-04 19:56:35', 0, 1);
/*!40000 ALTER TABLE `system_dept` ENABLE KEYS */;
UNLOCK
TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-09-05  9:49:27
