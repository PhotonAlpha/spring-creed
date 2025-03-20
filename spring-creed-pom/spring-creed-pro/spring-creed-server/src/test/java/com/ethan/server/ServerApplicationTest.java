package com.ethan.server;

import com.ethan.system.dal.entity.oauth2.CreedOAuth2Authorization;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2AuthorizationVO;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import com.ethan.system.dal.repository.oauth2.CreedOAuth2AuthorizationRepository;
import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CompoundSelection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

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
        /* CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CreedOAuth2AuthorizationVO> query = builder.createQuery(CreedOAuth2AuthorizationVO.class);X
        Root<CreedOAuth2Authorization> authRoot = query.from(CreedOAuth2Authorization.class);

        // CriteriaQuery<CreedOAuth2RegisteredClient> clientQuery = builder.createQuery(CreedOAuth2RegisteredClient.class);
        Root<CreedOAuth2RegisteredClient> clientRoot = query.from(CreedOAuth2RegisteredClient.class);

        Predicate joinCondition = builder.equal(authRoot.get("registeredClientId"), clientRoot.get("id"));
        query.orderBy(builder.desc(authRoot.get("id")));

        Selection<CreedOAuth2AuthorizationVO> selection = builder.construct(CreedOAuth2AuthorizationVO.class,
                authRoot.get("registeredClientId"), authRoot.get("principalName"),
                authRoot.get("accessTokenValue"), authRoot.get("accessTokenIssuedAt"),
                authRoot.get("refreshTokenValue"), authRoot.get("refreshTokenIssuedAt"),
                clientRoot.get("clientId")
        );


        query.select(selection).where(joinCondition);
        List<CreedOAuth2AuthorizationVO> resultList = entityManager.createQuery(query).getResultList();
        System.out.println(resultList); */

        Specification<CreedOAuth2AuthorizationVO> specification = new Specification<CreedOAuth2AuthorizationVO>() {
            @Override
            public Predicate toPredicate(Root<CreedOAuth2AuthorizationVO> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                CriteriaQuery<CreedOAuth2AuthorizationVO> newQuery = cb.createQuery(CreedOAuth2AuthorizationVO.class);
                Root<CreedOAuth2Authorization> authRoot = newQuery.from(CreedOAuth2Authorization.class);
                Root<CreedOAuth2RegisteredClient> clientRoot = newQuery.from(CreedOAuth2RegisteredClient.class);
                Predicate joinCondition = cb.equal(authRoot.get("registeredClientId"), clientRoot.get("id"));
                newQuery.orderBy(cb.desc(authRoot.get("id")));

                Selection<CreedOAuth2AuthorizationVO> selection = cb.construct(CreedOAuth2AuthorizationVO.class,
                        root.get("registeredClientId"), root.get("principalName"),
                        root.get("accessTokenValue"), root.get("accessTokenIssuedAt"),
                        root.get("refreshTokenValue"), root.get("refreshTokenIssuedAt"),
                        clientRoot.get("clientId")
                );
                // newQuery.select(selection).where(joinCondition);
                // query.from(CreedOAuth2AuthorizationVO.class).select(selection).where(joinCondition);
                newQuery.select(selection).where(joinCondition);
                new String("".getBytes(), StandardCharsets.UTF_8);
                return null;
            }

        };

        // Specification<CreedOAuth2Authorization> testSpec = new Specification<CreedOAuth2Authorization>() {
        //
        //     @Override
        //     public Predicate toPredicate(Root<CreedOAuth2Authorization> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        //         return null;
        //     }
        // };
        // creedOAuth2AuthorizationRepository.findBy(specification,q -> q.as(CreedOAuth2AuthorizationVO.class).all(), PageRequest.of(1, 10));
        // System.out.println(result);

    }
}
