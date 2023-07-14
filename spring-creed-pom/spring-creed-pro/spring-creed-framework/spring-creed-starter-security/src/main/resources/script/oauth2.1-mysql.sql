drop table if exists creed_oauth2_registered_client;
CREATE TABLE creed_oauth2_registered_client
(
    id                            varchar(100)                            NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamp,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    `status` int(1) DEFAULT 1,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `creed_oauth2_registered_client`
    ADD COLUMN `post_logout_redirect_uris` varchar(1000) DEFAULT NULL AFTER `redirect_uris`;

ALTER TABLE `creed_oauth2_registered_client`
    ADD COLUMN `enabled` INT(1) NOT NULL DEFAULT 0 AFTER `status`;
ALTER TABLE `creed_oauth2_registered_client`
    ADD COLUMN `create_time` timestamp NOT NULL DEFAULT current_timestamp();
ALTER TABLE `creed_oauth2_registered_client`
    ADD COLUMN `update_time` timestamp NOT NULL DEFAULT current_timestamp();
ALTER TABLE `creed_oauth2_registered_client`
    ADD COLUMN `creator` varchar(50) DEFAULT NULL;
ALTER TABLE `creed_oauth2_registered_client`
    ADD COLUMN `updater` varchar(50) DEFAULT NULL;
ALTER TABLE `creed_oauth2_registered_client`
    ADD COLUMN `version` int(11) NOT NULL DEFAULT 0;

drop table if exists creed_oauth2_authorization;
CREATE TABLE creed_oauth2_authorization
(
    id                            varchar(255) NOT NULL,
    registered_client_id          varchar(255) NOT NULL,
    principal_name                varchar(255) NOT NULL,
    authorization_grant_type      varchar(255) NOT NULL,
    authorized_scopes             varchar(1000) DEFAULT NULL,
    attributes                    blob          DEFAULT NULL,
    `state`                       varchar(500)  DEFAULT NULL,
    authorization_code_value      varchar(4000) DEFAULT NULL,
    authorization_code_issued_at  timestamp,
    authorization_code_expires_at timestamp,
    authorization_code_metadata   blob          DEFAULT NULL,
    access_token_value            varchar(4000) DEFAULT NULL,
    access_token_issued_at        timestamp,
    access_token_expires_at       timestamp,
    access_token_metadata         blob          DEFAULT NULL,
    access_token_type             varchar(255)  DEFAULT NULL,
    access_token_scopes           varchar(1000) DEFAULT NULL,
    oidc_id_token_value           varchar(4000) DEFAULT NULL,
    oidc_id_token_issued_at       timestamp,
    oidc_id_token_expires_at      timestamp,
    oidc_id_token_metadata        blob          DEFAULT NULL,
    oidc_id_token_claims          varchar(2000) DEFAULT NULL,
    refresh_token_value           varchar(4000) DEFAULT NULL,
    refresh_token_issued_at       timestamp,
    refresh_token_expires_at      timestamp,
    refresh_token_metadata        blob          DEFAULT NULL,

    user_code_value varchar(4000) DEFAULT NULL,
    user_code_issued_at timestamp DEFAULT NULL,
    user_code_expires_at timestamp DEFAULT NULL,
    user_code_metadata varchar(2000) DEFAULT NULL,
    device_code_value varchar(4000) DEFAULT NULL,
    device_code_issued_at timestamp DEFAULT NULL,
    device_code_expires_at timestamp DEFAULT NULL,
    device_code_metadata varchar(2000) DEFAULT NULL,

    `version`                     int(11) NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ALTER TABLE creed_oauth2_authorization ADD COLUMN  user_code_value VARCHAR(1000) DEFAULT NULL AFTER `refresh_token_metadata`;
-- ALTER TABLE creed_oauth2_authorization ADD COLUMN      user_code_issued_at timestamp  AFTER `user_code_value`;
-- ALTER TABLE creed_oauth2_authorization ADD COLUMN      user_code_expires_at timestamp  AFTER `user_code_issued_at`;
-- ALTER TABLE creed_oauth2_authorization ADD COLUMN      user_code_metadata VARCHAR(1000) DEFAULT NULL AFTER `user_code_expires_at`;
-- ALTER TABLE creed_oauth2_authorization ADD COLUMN      device_code_value VARCHAR(1000) DEFAULT NULL AFTER `user_code_metadata`;
-- ALTER TABLE creed_oauth2_authorization ADD COLUMN      device_code_issued_at timestamp  AFTER `device_code_value`;
-- ALTER TABLE creed_oauth2_authorization ADD COLUMN      device_code_expires_at timestamp AFTER `device_code_issued_at`;
-- ALTER TABLE creed_oauth2_authorization ADD COLUMN      device_code_metadata VARCHAR(1000) DEFAULT NULL AFTER `device_code_expires_at`;


drop table if exists creed_oauth2_authorization_consent;
CREATE TABLE creed_oauth2_authorization_consent
(
    registered_client_id varchar(100)  NOT NULL,
    principal_name       varchar(200)  NOT NULL,
    authorities          varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


DELETE FROM `creed_oauth2_registered_client`;
INSERT INTO `creed_oauth2_registered_client` (`id`, `client_id`, `client_id_issued_at`, `client_secret`,
                                        `client_secret_expires_at`, `client_name`, `client_authentication_methods`,
                                        `authorization_grant_types`, `redirect_uris`, `scopes`, `client_settings`,
                                        `token_settings`)
VALUES ('9dc45c80-e673-4215-9a19-329c161e08b8', 'messaging-client', '2023-12-02 06:42:51', '{noop}secret',
        '2023-12-02 06:42:51', '9dc45c80-e673-4215-9a19-329c161e08b8', 'client_secret_basic',
        'refresh_token,client_credentials,authorization_code',
        'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc',
        'openid,profile,message.read,message.write',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}');

-- {"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",210000.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",210000.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600000.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",9000.000000000]}


drop table if exists creed_oauth2_authorized_client;
CREATE TABLE creed_oauth2_authorized_client (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` varchar(200) NOT NULL COMMENT '用户编号',
    `user_type` tinyint(4) NOT NULL COMMENT '用户类型',
    client_registration_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    access_token_type varchar(100) NOT NULL,
    access_token_value blob NOT NULL,
    access_token_issued_at timestamp DEFAULT CURRENT_TIMESTAMP,
    access_token_expires_at timestamp DEFAULT CURRENT_TIMESTAMP,
    access_token_scopes varchar(1000) DEFAULT NULL,
    refresh_token_value blob DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT CURRENT_TIMESTAMP,
    refresh_token_expires_at timestamp DEFAULT CURRENT_TIMESTAMP,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`)
);

