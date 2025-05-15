package com.ethan.example.jpa.repository.oauth2;

import com.ethan.example.jpa.dal.oauth2.CreedOAuth2AuthorizationVO;
import com.ethan.example.jpa.dal.oauth2.CreedOauth2RegisteredClientVO;
import com.ethan.example.jpa.dal.permission.SystemAuthorities;
import com.ethan.example.jpa.vo.OAuth2AccessTokenPageReqVO;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ref {@see https://github.com/spring-projects/spring-data-jpa/issues/2113}
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 11/3/25
 */
public interface OAuth2AuthorizationVORepository extends JpaRepository<CreedOAuth2AuthorizationVO, String>, JpaSpecificationExecutor<CreedOAuth2AuthorizationVO> {


    default List<CreedOAuth2AuthorizationVO> findByCondition(OAuth2AccessTokenPageReqVO condition) {
        return findBy(
                (Specification<CreedOAuth2AuthorizationVO>) (root, query, cb) -> {
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
                }, query -> query.project("registeredClients").all()
        );
    }

}
