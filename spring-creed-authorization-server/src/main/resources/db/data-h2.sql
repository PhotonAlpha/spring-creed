DELETE FROM oauth_client_details;
INSERT INTO oauth_client_details (client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove) VALUES ('clientapp','{noop}112233','creed','read_userinfo,write_userinfo','authorization_code,refresh_token','http://localhost:8080/auth/grant','ROLE_TRUSTED_CLIENT',0,0,null ,'false');


-- http://localhost:8080/oauth/authorize?client_id=clientapp&redirect_uri=http://localhost:8080/auth/grant&response_type=code&scope=read_userinfo write_userinfo