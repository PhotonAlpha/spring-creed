package com.ethan;

import com.ethan.common.constant.SexEnum;
import com.ethan.entity.CreedAuthorities;
import com.ethan.entity.CreedConsumer;
import com.ethan.entity.CreedConsumerAuthorities;
import com.ethan.entity.CreedGroupAuthorities;
import com.ethan.entity.CreedGroupMembers;
import com.ethan.entity.CreedGroups;
import com.ethan.repository.CreedAuthorityRepository;
import com.ethan.repository.CreedConsumerAuthorityRepository;
import com.ethan.repository.CreedConsumerRepository;
import com.ethan.repository.CreedGroupAuthoritiesRepository;
import com.ethan.repository.CreedGroupsMembersRepository;
import com.ethan.repository.CreedGroupsRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@SpringBootTest(classes = TestingServerApplication.class)
public class TestingServerApplicationTest {
    @Autowired
    CreedConsumerRepository consumerRepository;
    @Autowired
    CreedAuthorityRepository authorityRepository;
    @Autowired
    CreedGroupsMembersRepository membersRepository;
    @Autowired
    CreedGroupsRepository groupsRepository;
    @Autowired
    CreedGroupAuthoritiesRepository groupAuthoritiesRepository;
    @Autowired
    CreedConsumerAuthorityRepository consumerAuthorityRepository;

    @Test
    void testWithoutGroup() throws InterruptedException {
        // saveData();
        // log.info("===end===");
        // TimeUnit.SECONDS.sleep(10);
        List<CreedConsumerAuthorities> authoritiesList = consumerAuthorityRepository.findByConsumerUsername("ethan");
        for (CreedConsumerAuthorities creedConsumerAuthorities : authoritiesList) {
            CreedConsumer consumer = creedConsumerAuthorities.getConsumer();
        }
    }
    @Transactional
    public void saveData() {
        CreedAuthorities authorities = new CreedAuthorities();
        authorities.setAuthority("COMMON2");
        authorities.setDescription("普通用户");
        authorityRepository.save(authorities);

        CreedConsumer consumer = consumerRepository.findByUsername("test2").orElse(null);
        if (consumer == null) {
            consumer = new CreedConsumer();
            consumer.setUsername("test2");
            consumer.setPassword("{noop}test2");
            consumer.setSex(SexEnum.MALE);
            consumer.setRemark("common");
            consumerRepository.save(consumer);
        }
        // int i = 1 / 0;
        CreedConsumerAuthorities consumerAuthorities = new CreedConsumerAuthorities(consumer, authorities);
        consumerAuthorityRepository.save(consumerAuthorities);

    }

    @Test
    // @Transactional
    void testJpa() {


        CreedGroupAuthorities groupAuthorities = new CreedGroupAuthorities();
        groupAuthorities.setAuthority("SuperADMIN");
        // groupAuthorities.setGroupId("2121");
        // groupAuthoritiesRepository.save(groupAuthorities);

        CreedConsumer consumer = consumerRepository.findByUsername("ethan").orElse(null);
        if (consumer == null) {
            consumer = new CreedConsumer();
            consumer.setUsername("ethan");
            consumer.setPassword("{noop}test");
            consumer.setSex(SexEnum.MALE);
            consumer.setRemark("admin");

            consumerRepository.save(consumer);
        }

        CreedGroupMembers members = new CreedGroupMembers();
        members.setUsername(consumer.getUsername());

        // creedGroupMembers.setConsumer(consumer);

        CreedGroups creedGroups = new CreedGroups();
        creedGroups.setGroupname("CEO");
        creedGroups.setAuthorities(List.of(groupAuthorities));
        creedGroups.setMembers(List.of(members));

        groupAuthorities.setGroups(creedGroups);
        members.setGroups(creedGroups);

        groupsRepository.save(creedGroups);

    }

    @Test
    void testConsumer() {
        // CreedAuthorities creedAuthorities = new CreedAuthorities();
        // CreedAuthorities creedAuthorities2 = new CreedAuthorities();
        // creedAuthorities.setAuthority("SUPER_ADMIN");
        // creedAuthorities2.setAuthority("TEST");

        CreedAuthorities creedAuthorities = authorityRepository.findByAuthority("SUPER_ADMIN").orElse(null);
        CreedAuthorities creedAuthorities2 = authorityRepository.findByAuthority("TEST").orElse(null);
        if (creedAuthorities == null) {
            creedAuthorities = new CreedAuthorities();
            creedAuthorities.setAuthority("SUPER_ADMIN");
            authorityRepository.save(creedAuthorities);
        }
        if (creedAuthorities2 == null) {
            creedAuthorities2 = new CreedAuthorities();
            creedAuthorities2.setAuthority("TEST");
            authorityRepository.save(creedAuthorities2);
        }


        CreedConsumer consumer = consumerRepository.findByUsername("ethan").orElse(null);
        if (consumer == null) {
            consumer = new CreedConsumer();
            consumer.setUsername("ethan");
            consumer.setPassword("{noop}test");
            consumer.setSex(SexEnum.MALE);
            consumer.setRemark("admin");

            consumerRepository.save(consumer);
        }

        CreedConsumerAuthorities authority1 = new CreedConsumerAuthorities(consumer, creedAuthorities);
        CreedConsumerAuthorities authority2 = new CreedConsumerAuthorities(consumer, creedAuthorities2);
        // consumerAuthorityRepository.save(authority1);
        // consumerAuthorityRepository.save(authority2);

        List<CreedConsumerAuthorities> consumers = consumerAuthorityRepository.findByConsumerId(consumer.getId()).orElse(Collections.emptyList());
        System.out.println("==");
        consumerAuthorityRepository.delete(consumers.get(1));
    }



}
