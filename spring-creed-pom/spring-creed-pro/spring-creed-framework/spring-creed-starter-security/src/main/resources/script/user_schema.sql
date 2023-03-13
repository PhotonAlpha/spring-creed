DROP TABLE IF EXISTS `creed_consumer_authorities`;
CREATE TABLE IF NOT EXISTS `creed_consumer_authorities` (
    `consumer_id` varchar(50) NOT NULL,
    `authority_id` varchar(50) NOT NULL,
    UNIQUE KEY `un_auth_consumer` (`consumer_id`,`authority_id`),
    KEY `ix_auth_username` (`consumer_id`,`authority_id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `creed_consumer_authorities`
    ADD COLUMN `enabled` INT(1) NOT NULL DEFAULT 0;
ALTER TABLE `creed_consumer_authorities`
    ADD COLUMN `create_time` timestamp NOT NULL DEFAULT current_timestamp();
ALTER TABLE `creed_consumer_authorities`
    ADD COLUMN `update_time` timestamp NOT NULL DEFAULT current_timestamp();
ALTER TABLE `creed_consumer_authorities`
    ADD COLUMN `creator` varchar(50) DEFAULT NULL;
ALTER TABLE `creed_consumer_authorities`
    ADD COLUMN `updater` varchar(50) DEFAULT NULL;

DROP TABLE IF EXISTS `creed_authorities`;
CREATE TABLE IF NOT EXISTS `creed_authorities` (
    `id` varchar(50) NOT NULL,
    `authority` varchar(50) NOT NULL,
    `description` varchar(500) DEFAULT NULL,
    `enabled` tinyint(1) NOT NULL DEFAULT 0,
    `sort` int(5) NOT NULL DEFAULT 0,

    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `creator` varchar(50) DEFAULT NULL,
    `update_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updater` varchar(50) DEFAULT NULL,
    `version` int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `creed_authorities`
    ADD COLUMN `remark` varchar(1000) DEFAULT NULL;
ALTER TABLE `creed_authorities`
    ADD COLUMN `type` varchar(1000) NOT NULL DEFAULT 1 COMMENT '角色类型',
ALTER TABLE `creed_authorities`
    ADD COLUMN `data_scope` tinyint(4) NOT NULL DEFAULT 1 COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）'
ALTER TABLE `creed_authorities`
    ADD COLUMN `data_scope_dept_ids` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '数据范围(指定部门数组)'

DROP TABLE IF EXISTS `creed_consumer`;
CREATE TABLE IF NOT EXISTS `creed_consumer` (
    `id` varchar(50) NOT NULL,
    `username` varchar(50) NOT NULL,
    `password` varchar(500) NOT NULL,
    `nickname` varchar(500) DEFAULT NULL,
    `remark` varchar(500) DEFAULT NULL,
    `email` varchar(500) DEFAULT NULL,
    `phone` varchar(50) DEFAULT NULL,
    `phone_code` varchar(10) DEFAULT NULL,
    `sex` tinyint(1) DEFAULT NULL,
    `avatar` varchar(2000) DEFAULT NULL,
    `enabled` tinyint(1) NOT NULL DEFAULT 0,
    `login_ip` varchar(2000) DEFAULT NULL,
    `login_date` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `acc_non_expired` tinyint(1) NOT NULL DEFAULT 0,
    `acc_non_locked` tinyint(1) NOT NULL DEFAULT 0,
    `credentials_non_expired` tinyint(1) NOT NULL DEFAULT 0,

    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `creator` varchar(50) DEFAULT NULL,
    `update_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updater` varchar(50) DEFAULT NULL,
    `version` int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- groups
DROP TABLE IF EXISTS `creed_group_authorities`;
DROP TABLE IF EXISTS `creed_group_members`;
DROP TABLE IF EXISTS `creed_groups`;

CREATE TABLE IF NOT EXISTS `creed_groups` (
    `id` varchar(50) NOT NULL,
    `groupname` varchar(50) NOT NULL,
    `parent_id` int(11) NOT NULL DEFAULT 0,
    `sort` int(5) NOT NULL DEFAULT 0,
    `remark` varchar(500) DEFAULT NULL,
    `email` varchar(500) DEFAULT NULL,
    `phone` varchar(50) DEFAULT NULL,
    `phone_code` varchar(10) DEFAULT NULL,
    `enabled` tinyint(1) NOT NULL DEFAULT 0,

    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `creator` varchar(50) DEFAULT NULL,
    `update_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updater` varchar(50) DEFAULT NULL,
    `version` int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `groupname` (`groupname`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `creed_group_authorities` (
    `group_id` varchar(50) NOT NULL,
    `authority` varchar(50) NOT NULL,
    `description` varchar(50) DEFAULT NULL,
    `sort` int(5) NOT NULL DEFAULT 0,
    `enabled` tinyint(1) NOT NULL DEFAULT 0,

    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `creator` varchar(50) DEFAULT NULL,
    `update_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updater` varchar(50) DEFAULT NULL,
    `version` int(11) NOT NULL DEFAULT 0,
    UNIQUE KEY `authority` (`authority`),
    KEY `fk_group_authorities_group` (`group_id`),
    CONSTRAINT `fk_group_authorities_group` FOREIGN KEY (`group_id`) REFERENCES `creed_groups` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `creed_group_members` (
    `id` varchar(50) NOT NULL,
    `username` varchar(50) NOT NULL,
    `group_id` varchar(50) NOT NULL,
    `enabled` tinyint(1) NOT NULL DEFAULT 0,

    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `creator` varchar(50) DEFAULT NULL,
    `update_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updater` varchar(50) DEFAULT NULL,
    `version` int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    KEY `fk_group_members_group` (`group_id`),
    CONSTRAINT `fk_group_members_group` FOREIGN KEY (`group_id`) REFERENCES `creed_groups` (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


