package com.ethan.system.dal.repository.oauth2.graph;

import com.ethan.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.ethan.system.dal.entity.oauth2.graph.CreedOAuth2AuthorizationVO;
import com.ethan.system.dal.entity.oauth2.graph.CreedOauth2RegisteredClientVO;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ref {@see https://github.com/spring-projects/spring-data-jpa/issues/2113}
 *
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 11/3/25
 */
public interface OAuth2AuthorizationGraphRepository extends JpaRepository<CreedOAuth2AuthorizationVO, String>, JpaSpecificationExecutor<CreedOAuth2AuthorizationVO> {


    default Page<CreedOAuth2AuthorizationVO> findByCondition(OAuth2AccessTokenPageReqVO condition, Pageable pageable) {
        return findBy(buidSpecification(condition),
                query -> query.project("registeredClients").page(pageable)
        );
    }

    default List<CreedOAuth2AuthorizationVO> findByCondition(OAuth2AccessTokenPageReqVO condition) {
        return findBy(buidSpecification(condition),
                query -> query.project("registeredClients").all()
        );
    }

    default Specification<CreedOAuth2AuthorizationVO> buidSpecification(OAuth2AccessTokenPageReqVO condition) {
        return (root, query, cb) -> {
            Join<CreedOAuth2AuthorizationVO, CreedOauth2RegisteredClientVO> joiner = root.join("registeredClients");
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.hasText(condition.getUserName())) {
                predicateList.add(cb.like(cb.lower(root.get("principalName").as(String.class)),
                        "%" + condition.getUserName().toLowerCase() + "%"));
            }
            if (Objects.nonNull(condition.getClientId())) {
                predicateList.add(cb.equal(joiner.get("clientId"), condition.getClientId()));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
    }

}
