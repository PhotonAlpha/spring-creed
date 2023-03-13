package com.ethan.server;

import com.ethan.common.constant.SexEnum;
import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.security.websecurity.entity.CreedConsumer;
import com.ethan.security.websecurity.repository.CreedAuthorityRepository;
import com.ethan.security.websecurity.repository.CreedConsumerRepository;
import com.ethan.security.websecurity.repository.CreedGroupsMembersRepository;
import com.ethan.security.websecurity.repository.CreedGroupsRepository;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Set;

@SpringBootTest(classes = ServerApplication.class)
public class ServerApplicationTest {
    @Autowired
    JWKSource<SecurityContext> jwkSource;
    @Autowired
    JwtDecoder jwtDecoder;
    @Autowired
    CreedConsumerRepository consumerRepository;
    @Autowired
    CreedAuthorityRepository authorityRepository;
    @Autowired
    CreedGroupsMembersRepository membersRepository;
    @Autowired
    CreedGroupsRepository groupsRepository;

    @Test
    void jwkTesting() {
        Jwt decode = jwtDecoder.decode("eyJraWQiOiIzMDVmNzBiMS1kNmYyLTQ1NzItYTJkNC1hMmU5ZmJjOGI3MWEiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJtZXNzYWdpbmctY2xpZW50IiwiYXVkIjoibWVzc2FnaW5nLWNsaWVudCIsIm5iZiI6MTY2OTk0NjQ4MCwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwiZXhwIjoxNjY5OTQ2NzgwLCJpYXQiOjE2Njk5NDY0ODB9.UoFygcgiSFhYqAq7-dqMskKI34WaUiBILqXxXFhTxtedCIaAMozVABAABYEFkuStXSOo_amU4lJqRMiEGZxoabj5rpobb0gCE3pgwbLIKBuMYe8ly7mFYUZeh3JOzfMH3mXzTOuwTniEFiZ9cnAHWDAxBK6CGUDh6yVFZWocAob-w68bb6-2vgMZ8zQsm1JfjmRtiQaWMrbNkIrkM65OhCx47QTBI3eCvgeYy6yhAIRO0FQghyJRQMMjZud4YmqLKS5QT2VW-bH7u2If4zO-ukfRgonsw7gMulcDy3N2Hhq3jjmkgtjpU96sM_YWzHDsHdixQIwzGUuQrqiai0vv9w");
        System.out.println(decode);
    }

    @Test
    void testJpa() {
        CreedAuthorities creedAuthorities = new CreedAuthorities();
        CreedAuthorities creedAuthorities2 = new CreedAuthorities();
        creedAuthorities.setAuthority("SUPER_ADMIN");
        creedAuthorities2.setAuthority("TEST");
        Set<CreedAuthorities> authorities = Set.of(creedAuthorities, creedAuthorities2);

        CreedConsumer consumer = new CreedConsumer();
        consumer.setUsername("ethan");
        consumer.setPassword("{noop}test");
        consumer.setSex(SexEnum.MALE);
        consumer.setRemark("admin");
        // consumer.setConsumerAuthorities(List.of(authorities.toArray(CreedAuthorities[]::new)));
        consumerRepository.save(consumer);

    }
}
