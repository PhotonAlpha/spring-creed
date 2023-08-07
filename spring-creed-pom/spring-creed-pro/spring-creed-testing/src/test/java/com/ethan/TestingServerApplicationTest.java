package com.ethan;

import com.ethan.common.constant.SexEnum;
import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.security.websecurity.entity.CreedUser;
import com.ethan.security.websecurity.repository.CreedAuthorityRepository;
import com.ethan.security.websecurity.repository.CreedGroupsMembersRepository;
import com.ethan.security.websecurity.repository.CreedGroupsRepository;
import com.ethan.security.websecurity.repository.CreedUserRepository;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@Slf4j
@SpringBootTest(classes = TestingServerApplication.class)
public class TestingServerApplicationTest {


    @Autowired
    CreedUserRepository consumerRepository;
    @Autowired
    CreedAuthorityRepository authorityRepository;
    @Autowired
    CreedGroupsMembersRepository membersRepository;
    @Autowired
    CreedGroupsRepository groupsRepository;

    @Test
    void testQueryUser() {
        CreedUser ethan2 = consumerRepository.findByUsername("ethan2").get();
    }

    @Test
    @Rollback(false)
    @Transactional
    void testCreateUser() {
        CreedAuthorities creedAuthorities = new CreedAuthorities();
        CreedAuthorities creedAuthorities2 = new CreedAuthorities();
        CreedAuthorities creedAuthorities3 = new CreedAuthorities();
        creedAuthorities.setAuthority("SUPER_ADMIN");
        creedAuthorities2.setAuthority("TEST");
        creedAuthorities3.setAuthority("ADMIN");

        Set<CreedAuthorities> authorities =
                Sets.newHashSet(
                        authorityRepository.findByAuthority(creedAuthorities.getAuthority()).orElse(creedAuthorities),
                        authorityRepository.findByAuthority(creedAuthorities2.getAuthority()).orElse(creedAuthorities2),
                        authorityRepository.findByAuthority(creedAuthorities3.getAuthority()).orElse(creedAuthorities3)
                );
        authorityRepository.saveAll(authorities);

        CreedUser user = new CreedUser();
        user.setUsername("ethan");
        user.setPassword("{bcrypt}$2a$10$vvGtEfcMR.QXhGr5pSzGFezYALQrDVO8Xrm8meTlH1gsQ3fqjNLSm");//pwd
        user.setSex(SexEnum.MALE);
        user.setRemark("admin");
        user.setAuthorities(authorities);

        consumerRepository.save(user);

    }
    @Test
    @Rollback(false)
    @Transactional
    void testUpdateUser() {
        CreedAuthorities creedAuthorities = new CreedAuthorities();
        CreedAuthorities creedAuthorities2 = new CreedAuthorities();
        creedAuthorities.setAuthority("SUPER_ADMIN");
        creedAuthorities2.setAuthority("TEST");

        CreedAuthorities creedAuthorities3 = new CreedAuthorities();
        creedAuthorities3.setAuthority("ADMIN");
        // authorityRepository.save(creedAuthorities3);

        Set<CreedAuthorities> authorities =
                Sets.newHashSet(
                        authorityRepository.findByAuthority(creedAuthorities.getAuthority()).get(),
                        authorityRepository.findByAuthority(creedAuthorities3.getAuthority()).get()
                );
        CreedUser ethan2 = consumerRepository.findByUsername("ethan2").get();
        ethan2.setAuthorities(authorities);

        consumerRepository.save(ethan2);

    }
    @Test
    @Rollback(false)
    @Transactional
    void testDeleteUser() {
        CreedUser ethan2 = consumerRepository.findByUsername("ethan2").get();
        consumerRepository.delete(ethan2);

    }
    @Test
    @Rollback(false)
    @Transactional
    void testDeleteAuthorities() {
        CreedAuthorities authorities = authorityRepository.findByAuthority("ADMIN").get();
        authorityRepository.delete(authorities);

    }

    /**
     * DELETE FROM `creed_oauth2_registered_client`;
     * INSERT INTO `creed_oauth2_registered_client` (`id`, `client_id`, `client_id_issued_at`, `client_secret`,
     *                                         `client_secret_expires_at`, `client_name`, `client_authentication_methods`,
     *                                         `authorization_grant_types`, `redirect_uris`, `scopes`, `client_settings`,
     *                                         `token_settings`)
     * VALUES ('9dc45c80-e673-4215-9a19-329c161e08b8', 'messaging-client', '2023-12-02 06:42:51', '{noop}secret',
     *         '2023-12-02 06:42:51', '9dc45c80-e673-4215-9a19-329c161e08b8', 'client_secret_basic',
     *         'refresh_token,client_credentials,authorization_code',
     *         'http://127.0.0.1:8080/authorized,http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc',
     *         'openid,profile,message.read,message.write',
     *         '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
     *         '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000]}');
     */
    @Test
    void createOauth2Client() {
//        this.settings = Collections.unmodifiableMap(new HashMap<>(settings));
    }
}
