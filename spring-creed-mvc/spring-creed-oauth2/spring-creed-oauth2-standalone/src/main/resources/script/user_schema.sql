DROP TABLE IF EXISTS `e_authorities`;
CREATE TABLE IF NOT EXISTS `e_authorities` (
    `username` varchar(50) NOT NULL,
    `authority` varchar(50) NOT NULL,
    PRIMARY KEY (`username`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.

-- Dumping structure for table auth.e_users
DROP TABLE IF EXISTS `e_users`;
CREATE TABLE IF NOT EXISTS `e_users` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `username` varchar(50) NOT NULL,
    `password` varchar(500) NOT NULL,
    `enabled` tinyint(1) NOT NULL,
    `acc_expired` tinyint(1) DEFAULT NULL,
    `acc_locked` tinyint(1) DEFAULT NULL,
    `creds_expired` tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`)
    ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


INSERT INTO `e_authorities` VALUES ('ethan', 'ROLE_USER');
INSERT INTO `e_users` VALUES ('ethan', '{noop}112233', '1', '1', '1', '1');