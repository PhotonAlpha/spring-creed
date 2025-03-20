drop table if exists creed_oauth2_registered_client;
CREATE TABLE creed_oauth2_registered_client
(
    id                            varchar(255)                        not null primary key,
    client_id                     varchar(255)                        null,
    client_id_issued_at           timestamp default CURRENT_TIMESTAMP not null,
    client_secret                 varchar(255)                        null,
    client_secret_expires_at      timestamp                           null,
    client_name                   varchar(255)                        null,
    client_authentication_methods varchar(1000)                       not null,
    authorization_grant_types     varchar(1000)                       not null,
    redirect_uris                 varchar(1000)                       null,
    post_logout_redirect_uris     varchar(1000)                       null,
    scopes                        varchar(1000)                       not null,
    client_settings               varchar(2000)                       not null,
    token_settings                varchar(2000)                       not null,
    status                        int       default 1                 null,
    enabled                       int       default 0                 not null,
    create_time                   timestamp default CURRENT_TIMESTAMP not null,
    update_time                   timestamp default CURRENT_TIMESTAMP not null,
    creator                       varchar(255)                        null,
    updater                       varchar(255)                        null,
    version                       int       default 0                 not null,
    deleted                       int                                 null,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ALTER TABLE `creed_oauth2_registered_client`
--     ADD COLUMN `post_logout_redirect_uris` varchar(1000) DEFAULT NULL AFTER `redirect_uris`;
--
-- ALTER TABLE `creed_oauth2_registered_client`
--     ADD COLUMN `enabled` INT(1) NOT NULL DEFAULT 0 AFTER `status`;
-- ALTER TABLE `creed_oauth2_registered_client`
--     ADD COLUMN `create_time` timestamp NOT NULL DEFAULT current_timestamp();
-- ALTER TABLE `creed_oauth2_registered_client`
--     ADD COLUMN `update_time` timestamp NOT NULL DEFAULT current_timestamp();
-- ALTER TABLE `creed_oauth2_registered_client`
--     ADD COLUMN `creator` varchar(50) DEFAULT NULL;
-- ALTER TABLE `creed_oauth2_registered_client`
--     ADD COLUMN `updater` varchar(50) DEFAULT NULL;
-- ALTER TABLE `creed_oauth2_registered_client`
--     ADD COLUMN `version` int(11) NOT NULL DEFAULT 0;

drop table if exists creed_oauth2_authorization;
CREATE TABLE creed_oauth2_authorization
(
    id                            varchar(255)  not null primary key,
    registered_client_id          varchar(255)  not null,
    principal_name                varchar(255)  not null,
    authorization_grant_type      varchar(255)  not null,
    authorized_scopes             varchar(500)  null,
    attributes                    text          null,
    state                         varchar(500)  null,
    authorization_code_value      varchar(1000) null,
    authorization_code_issued_at  timestamp     null,
    authorization_code_expires_at timestamp     null,
    authorization_code_metadata   text          null,
    access_token_value            varchar(1000) null,
    access_token_issued_at        timestamp     null,
    access_token_expires_at       timestamp     null,
    access_token_metadata         text          null,
    access_token_type             varchar(255)  null,
    access_token_scopes           varchar(100)  null,
    oidc_id_token_value           varchar(1000) null,
    oidc_id_token_issued_at       timestamp     null,
    oidc_id_token_expires_at      timestamp     null,
    oidc_id_token_metadata        text          null,
    oidc_id_token_claims          text          null,
    refresh_token_value           varchar(1000) null,
    refresh_token_issued_at       timestamp     null,
    refresh_token_expires_at      timestamp     null,
    refresh_token_metadata        text          null,
    user_code_value               varchar(1000) null,
    user_code_issued_at           timestamp     null,
    user_code_expires_at          timestamp     null,
    user_code_metadata            varchar(500)  null,
    device_code_value             varchar(1000) null,
    device_code_issued_at         timestamp     null,
    device_code_expires_at        timestamp     null,
    device_code_metadata          varchar(200)  null,
    version                       int default 0 not null,
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
    registered_client_id varchar(255)  NOT NULL,
    principal_name       varchar(255)  NOT NULL,
    authorities          varchar(1000) NOT NULL,
    PRIMARY KEY (registered_client_id, principal_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


DELETE FROM `creed_oauth2_registered_client`;
INSERT INTO `creed_oauth2_registered_client` (`id`, `client_id`, `client_id_issued_at`, `client_secret`,
                                        `client_secret_expires_at`, `client_name`, `client_authentication_methods`,
                                        `authorization_grant_types`, `redirect_uris`, `scopes`, `client_settings`,
                                        `token_settings`)
VALUES ('9dc45c80-e673-4215-9a19-329c161e08b8', 'messaging-client', '2023-12-02 06:42:51', '{noop}secret',
        '2033-12-02 06:42:51', '9dc45c80-e673-4215-9a19-329c161e08b8', 'client_secret_basic',
        'refresh_token,client_credentials,authorization_code',
        'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc',
        'openid,profile,message.read,message.write',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}');

-- {"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",210000.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",210000.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600000.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",9000.000000000]}
-- password
INSERT INTO creed_oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name, client_authentication_methods, authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings, status, enabled, create_time, update_time, creator, updater, version, deleted) VALUES ('3a86b814-682d-4667-be11-2c6d90b52914', 'random-client', '2024-10-16 09:26:26', '{bcrypt}$2a$10$OicsQVQsuj49E4E6nXeXZ.O2pyQKybYUYV69OohgX.CSATXdlaRim', '2029-10-16 09:26:26', 'randomToken', 'client_secret_basic,client_secret_post,client_secret_jwt,private_key_jwt,none,tls_client_auth,self_signed_tls_client_auth', 'refresh_token,authorization_code,client_credentials,urn:ietf:params:oauth:grant-type:jwt-bearer,urn:ietf:params:oauth:grant-type:device_code,urn:ietf:params:oauth:grant-type:token-exchange', 'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc', null, 'openid,profile,message.read,message.write', '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration",604800.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",3600.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",3600.000000000]}', 1, 0, '2024-10-16 09:26:26', '2024-10-16 09:26:26', 'default', 'default', 0, 0);
INSERT INTO creed_oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name, client_authentication_methods, authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings, status, enabled, create_time, update_time, creator, updater, version, deleted) VALUES ('65b4ce3c-77a8-494c-9fff-3743675e4796', 'jwt-client', '2024-10-16 09:26:41', '{bcrypt}$2a$10$vPsLY69f5e21gLn7Cs027Ordj6Il6wFfbYeHHLOgmiPkH0/dEMMcG', '2029-10-16 09:26:41', 'jwtToken', 'client_secret_basic,client_secret_post,client_secret_jwt,private_key_jwt,none,tls_client_auth,self_signed_tls_client_auth', 'refresh_token,authorization_code,client_credentials,urn:ietf:params:oauth:grant-type:jwt-bearer,urn:ietf:params:oauth:grant-type:device_code,urn:ietf:params:oauth:grant-type:token-exchange', 'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc', null, 'openid,profile,message.read,message.write', '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",86400.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",604800.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",3600.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",3600.000000000]}', 1, 0, '2024-10-16 09:26:41', '2024-10-16 09:26:41', 'default', 'default', 0, 0);
INSERT INTO creed_oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name, client_authentication_methods, authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings, status, enabled, create_time, update_time, creator, updater, version, deleted) VALUES ('9dc45c80-e673-4215-9a19-329c161e08b0', 'default-client', '2023-12-02 06:42:51', '{noop}secret', '2033-12-02 06:42:51', 'random-token', 'client_secret_basic,client_secret_post', 'refresh_token,client_credentials,authorization_code', 'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc', null, 'openid,profile,message.read,message.write', '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",36000.0000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"reference"},"settings.token.refresh-token-time-to-live":["java.time.Duration",36000.00000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}', 1, 0, '2024-07-01 18:03:54', '2024-07-01 18:03:54', null, null, 0, 0);
INSERT INTO creed_oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name, client_authentication_methods, authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings, status, enabled, create_time, update_time, creator, updater, version, deleted) VALUES ('9dc45c80-e673-4215-9a19-329c161e08b8', 'messaging-client', '2023-12-02 06:42:51', '{bcrypt}$2a$10$Y4fBsDG0.rUAh/uWqeKT7uonfc/BxaK3Y7VrlX0d86HciEmjcGfhq', '2033-12-02 06:42:51', '9dc45c80-e673-4215-9a19-329c161e08b8', 'client_secret_post,client_secret_basic', 'refresh_token,client_credentials,authorization_code', 'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc', null, 'openid,profile,message.read,message.write', '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}', 1, 0, '2024-07-01 18:03:54', '2024-09-13 09:51:10', null, null, 0, 0);
INSERT INTO creed_oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name, client_authentication_methods, authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings, status, enabled, create_time, update_time, creator, updater, version, deleted) VALUES ('9dc45c80-e673-4215-9a19-329c161e08b9', 'default', '2023-12-02 06:42:51', '{noop}secret', '2033-12-02 06:42:51', 'jwt-token', 'client_secret_basic,client_secret_post', 'refresh_token,client_credentials,authorization_code', 'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc', null, 'openid,profile,message.read,message.write', '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",36000.0000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",36000.00000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}', 1, 0, '2024-07-01 18:03:54', '2024-07-01 18:03:54', null, null, 0, 0);
INSERT INTO creed_oauth2_registered_client (id, client_id, client_id_issued_at, client_secret, client_secret_expires_at, client_name, client_authentication_methods, authorization_grant_types, redirect_uris, post_logout_redirect_uris, scopes, client_settings, token_settings, status, enabled, create_time, update_time, creator, updater, version, deleted) VALUES ('e860f9fe-9817-45b6-a34d-a487aa712f3b', 'test', '2024-12-09 08:22:44', '{bcrypt}$2a$10$vwZ.X8eHdaZ6yVBCxaYKJuYtYcZcQ2LVETVmz.uTy8727Dcifw7gq', '2029-12-09 08:22:44', 'nice', 'client_secret_basic', 'client_credentials', 'http://localhost/system/oauth2/oauth2/application', null, 'data.read,data.write', '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}', '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.x509-certificate-bound-access-tokens":false,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",1800.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",43200.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.device-code-time-to-live":["java.time.Duration",300.000000000]}', 1, 0, '2024-12-09 08:22:44', '2024-12-09 08:22:44', 'default', 'default', 0, 0);


drop table if exists creed_oauth2_authorized_client;
CREATE TABLE creed_oauth2_authorized_client (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `user_id` varchar(200) NOT NULL COMMENT '用户编号',
    `user_type` tinyint(4) NOT NULL COMMENT '用户类型',
    client_registration_id varchar(100) NOT NULL,
    principal_name varchar(200) NOT NULL,
    access_token_type varchar(100) NOT NULL,
    access_token_value text NOT NULL,
    access_token_issued_at timestamp DEFAULT CURRENT_TIMESTAMP,
    access_token_expires_at timestamp DEFAULT CURRENT_TIMESTAMP,
    access_token_scopes varchar(1000) DEFAULT NULL,
    refresh_token_value text DEFAULT NULL,
    refresh_token_issued_at timestamp DEFAULT CURRENT_TIMESTAMP,
    refresh_token_expires_at timestamp DEFAULT CURRENT_TIMESTAMP,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (`id`)
);


-- 客户端Oauth2Client数据加载表，用来获取OAUTH2 Server的token
drop table if exists creed_oauth2_client_configuration;
CREATE TABLE creed_oauth2_client_configuration (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `registration_id` varchar(200) NOT NULL COMMENT '注册id',
    `authorization_uri` varchar(200) DEFAULT NULL,
    `token_uri` varchar(200) NOT NULL,
    `user_info_uri` varchar(200) DEFAULT NULL,
    `user_info_authentication_method` varchar(200) DEFAULT NULL,
    `user_name_attribute_name` varchar(200) DEFAULT NULL,
    `jwk_set_uri` varchar(200) DEFAULT NULL,
    `issuer_uri` varchar(200) DEFAULT NULL,
    `configuration_metadata` varchar(200) DEFAULT NULL,
    `client_id` varchar(200) NOT NULL COMMENT '用户类型',
    `client_secret` varchar(200) NOT NULL,
    client_authentication_method varchar(500) NOT NULL,
    authorization_grant_type varchar(100) NOT NULL,
    redirect_uri varchar(500) DEFAULT NULL,
    scopes varchar(500) NOT NULL,
    client_name varchar(500) DEFAULT NULL,
    `create_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `creator` varchar(50) DEFAULT NULL,
    `update_time` timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    `updater` varchar(50) DEFAULT NULL,
    `deleted` tinyint(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
);

insert into `creed_oauth2_client_configuration` (`registration_id`, `token_uri`, `client_id`, `client_secret`,
                                                 client_authentication_method, authorization_grant_type, `scopes`,
                                                 `client_name`, `creator`, `updater`)
values ('okta', 'http://localhost:48080/oauth2/token', 'default', 'secret', 'client_secret_basic', 'client_credentials',
        'message.read,message.write', 'default client credentials', 'system', 'system');
