DROP TABLE IF EXISTS `e_authorities`;
CREATE TABLE `e_authorities` (
                               `username` varchar(50) NOT NULL,
                               `authority` varchar(50) NOT NULL,
                               UNIQUE KEY `ix_auth_username` (`username`,`authority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `e_users`;
CREATE TABLE `e_users` (
                         `username` varchar(50) NOT NULL,
                         `password` varchar(500) NOT NULL,
                         `enabled` tinyint(1) NOT NULL,
                         PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `e_authorities` VALUES ('ethan', 'ROLE_USER');
INSERT INTO `e_users` VALUES ('ethan', '{noop}112233', '1');