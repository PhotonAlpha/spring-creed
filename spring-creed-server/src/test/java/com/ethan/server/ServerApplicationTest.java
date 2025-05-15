package com.ethan.server;

import com.ethan.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2Authorization;
import com.ethan.system.dal.repository.oauth2.CreedOAuth2AuthorizationRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = ServerApplication.class)
@Transactional
public class ServerApplicationTest {
    @Autowired
    CreedOAuth2AuthorizationRepository creedOAuth2AuthorizationRepository;

    @Resource
    EntityManager entityManager;

    @Test
    @Rollback(false)
    void creedOAuth2AuthorizationRepositoryTest() {
        var reqVO = new OAuth2AccessTokenPageReqVO();
        reqVO.setPageNo(1);
        reqVO.setPageSize(10);
        Page<CreedOAuth2Authorization> result = creedOAuth2AuthorizationRepository.findByCondition(reqVO);
        System.out.println(result.getContent());

    }
}
