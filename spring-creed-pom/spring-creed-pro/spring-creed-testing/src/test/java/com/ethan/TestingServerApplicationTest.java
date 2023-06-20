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




}
