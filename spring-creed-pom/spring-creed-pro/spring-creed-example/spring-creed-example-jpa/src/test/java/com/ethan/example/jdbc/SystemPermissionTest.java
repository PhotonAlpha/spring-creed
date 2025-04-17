package com.ethan.example.jdbc;


import com.ethan.example.JpaExampleApplication;
import com.ethan.example.jdbc.dal.permission.CreedOAuth2AuthorizationVO;
import com.ethan.example.jdbc.repository.OAuth2AuthorizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(classes = JpaExampleApplication.class)
@Transactional
@Slf4j
public class SystemPermissionTest {
    @Autowired
    OAuth2AuthorizationRepository oAuth2AuthorizationRepository;


    @Test
    void findByConditionTest() {
        var condition = new CreedOAuth2AuthorizationVO();
        condition.setRegisteredClientId("65b4ce3c-77a8-494c-9fff-3743675e4796");
        condition.setId("65b4ce3c-77a8-494c-9fff-3743675e4796");
        ExampleMatcher matcher = ExampleMatcher.matching()
                // .withMatcher("registeredClientId", exact())
                .withIgnorePaths("id")
                .withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT);
        Example<CreedOAuth2AuthorizationVO> example = Example.of(condition, matcher);

        List<CreedOAuth2AuthorizationVO> result = oAuth2AuthorizationRepository.findByCondition(example);
        System.out.println(result);
    }
    @Test
    void findByPrincipalNameTest() {
        List<CreedOAuth2AuthorizationVO> result = oAuth2AuthorizationRepository.findByPrincipalName("jwt-client");
        System.out.println(result);
    }
    @Test
    void dynamicQueryTest() {
        Page<CreedOAuth2AuthorizationVO> result = oAuth2AuthorizationRepository.findAll(PageRequest.of(0, 10));
        System.out.println(result);
    }

    /**
     * {@ default implementation TypedExampleMatcher#TypedExampleMatcher()}
     */
    @Test
    void queryDSLTest() {
        var condition = new CreedOAuth2AuthorizationVO();
        condition.setRegisteredClientId("65b4ce3c-77a8-494c-9fff-3743675e4796");
        condition.setId("65b4ce3c-77a8-494c-9fff-3743675e4796");
        //
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT);


        // ExampleMatcher matcher = ExampleMatcher.matching()
        //         .withIgnorePaths("lastname")
        //         .withIncludeNullValues()
        //         .withStringMatcher(StringMatcher.ENDING);
        Example<CreedOAuth2AuthorizationVO> example = Example.of(condition, matcher);
        Iterable<CreedOAuth2AuthorizationVO> result = oAuth2AuthorizationRepository.findAll(example);
        System.out.println(result);
    }
    @Test
    void fluentQueryTest() {
        var condition = new CreedOAuth2AuthorizationVO();
        condition.setRegisteredClientId("65b4ce3c-77a8-494c-9fff-3743675e4796");
        condition.setPrincipalName("ethan");
        condition.setId("65b4ce3c-77a8-494c-9fff-3743675e4796");
        ExampleMatcher matcher = ExampleMatcher.matching()
                // .withMatcher("registeredClientId", exact())
                .withIgnorePaths("id")
                .withStringMatcher(ExampleMatcher.StringMatcher.DEFAULT);
        Example<CreedOAuth2AuthorizationVO> example = Example.of(condition, matcher);
        List<CreedOAuth2AuthorizationVO> result = oAuth2AuthorizationRepository.findBy(example, q ->
                q.project("principalName")
                        .sortBy(Sort.by("id").descending()).all()
        );
        System.out.println(result);
    }
}
