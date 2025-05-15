package com.ethan.example.jpa;

import com.ethan.example.JpaExampleApplication;
import com.ethan.example.jpa.dal.oauth2.CreedOAuth2AuthorizationVO;
import com.ethan.example.jpa.dal.oauth2.CreedOauth2RegisteredClientVO;
import com.ethan.example.jpa.repository.oauth2.OAuth2AuthorizationVORepository;
import com.ethan.example.jpa.vo.OAuth2AccessTokenPageReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 11/3/25
 */
@SpringBootTest(classes = JpaExampleApplication.class)
@Slf4j
public class OAuth2AuthorizationVORepositoryTest {
    @Resource
    private OAuth2AuthorizationVORepository auth2AuthorizationVORepository;

    @Test
    void finaAll() {
        List<CreedOAuth2AuthorizationVO> all = auth2AuthorizationVORepository.findAll();
        for (var vo : all) {
            log.info("vo:{} PrincipalName:{} ClientId:{}", vo.getId(), vo.getPrincipalName(), Optional.ofNullable(vo.getRegisteredClients()).map(CreedOauth2RegisteredClientVO::getClientId).orElse("NA"));
        }
    }

    @Test
    void findAllByCondition() {
        OAuth2AccessTokenPageReqVO condition = new OAuth2AccessTokenPageReqVO();
        condition.setUserName("ethan");
        List<CreedOAuth2AuthorizationVO> all = auth2AuthorizationVORepository.findByCondition(condition);
        for (var vo : all) {
            log.info("vo:{} PrincipalName:{} ClientId:{}", vo.getId(), vo.getPrincipalName(), Optional.ofNullable(vo.getRegisteredClients()).map(CreedOauth2RegisteredClientVO::getClientId).orElse("NA"));
        }
        condition.setClientId("65b4ce3c-77a8-494c-9fff-3743675e4796");
        all = auth2AuthorizationVORepository.findByCondition(condition);
        for (var vo : all) {
            log.info("vo:{} PrincipalName:{} ClientId:{}", vo.getId(), vo.getPrincipalName(), Optional.ofNullable(vo.getRegisteredClients()).map(CreedOauth2RegisteredClientVO::getClientId).orElse("NA"));
        }

    }
    @Test
    // @Transactional
    void saveImmutableTest() {
        CreedOAuth2AuthorizationVO vo = new CreedOAuth2AuthorizationVO();
        vo.setPrincipalName("test");
        vo.setId(UUID.randomUUID().toString());
        auth2AuthorizationVORepository.save(vo);
    }
}
