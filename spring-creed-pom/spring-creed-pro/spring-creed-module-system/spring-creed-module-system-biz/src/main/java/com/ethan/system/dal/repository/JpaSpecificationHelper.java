package com.ethan.system.dal.repository;

import com.ethan.system.dal.entity.oauth2.CreedOAuth2Authorization;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2AuthorizationVO;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 3/1/25
 */
@Component
public class JpaSpecificationHelper {
    private final EntityManager entityManager;

    public JpaSpecificationHelper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T> Page<T> findAll(Specification<T> specification, Pageable pageable, Stream<String> customSelect, Class<T> type) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> builderQuery = criteriaBuilder.createQuery(type);
        Root<T> root = builderQuery.from(type);
        Predicate where = specification.toPredicate(
                root,
                builderQuery,
                criteriaBuilder
        );
        List<Order> orders = new ArrayList<>();
        for (Sort.Order sortOrder : pageable.getSort().toList()) {
            Order order;
            if (sortOrder.getDirection() == Sort.Direction.DESC) {
                order = criteriaBuilder.desc(root.get(sortOrder.getProperty()));
            } else {
                order = criteriaBuilder.asc(root.get(sortOrder.getProperty()));
            }
            orders.add(order);
        }
        builderQuery = builderQuery
                .multiselect(customSelect
                        .map(root::get)
                        .collect(Collectors.toList())
                )
                .where(where)
                .orderBy(orders);
        TypedQuery<T> typedQuery = entityManager.createQuery(builderQuery);
        typedQuery.setFirstResult(pageable.getPageNumber());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<T> resultList = typedQuery.getResultList();

        return new PageImpl<>(resultList, pageable, getTotal(where, type));
    }

    public List<CreedOAuth2AuthorizationVO> findAll() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CreedOAuth2AuthorizationVO> criteria = builder.createQuery(CreedOAuth2AuthorizationVO.class);
        Root<CreedOAuth2Authorization> authRoot = criteria.from(CreedOAuth2Authorization.class);
        Root<CreedOAuth2RegisteredClient> clientRoot = criteria.from(CreedOAuth2RegisteredClient.class);
        Predicate joinCondition = builder.equal(authRoot.get("registeredClientId"), clientRoot.get("id"));
        List<Predicate> predicateList = new ArrayList<>();
        predicateList.add(joinCondition);
        // if (StringUtils.isNotBlank(reqVO.getUserName())) {
        //     // 本处都转为小写，进行模糊匹配
        //     predicateList.add(cb.like(authRoot.get("principalName"), "%" + reqVO.getUserName() + "%"));
        // }
        // if (StringUtils.isNotBlank(reqVO.getClientId())) {
        //     predicateList.add(cb.equal(clientRoot.get("clientId"), reqVO.getClientId()));
        // }
        criteria.orderBy(builder.desc(authRoot.get("id")));
        Selection<CreedOAuth2AuthorizationVO> registeredClientId = builder.construct(CreedOAuth2AuthorizationVO.class, authRoot.get("registeredClientId"));
        criteria.select(registeredClientId);
        criteria.where(predicateList.toArray(new Predicate[0]));
        TypedQuery<CreedOAuth2AuthorizationVO> query = entityManager.createQuery(criteria);
        return query.getResultList();
    }

    private <T> Long getTotal(Predicate where, Class<T> type) {
        CriteriaBuilder qb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(type)));
        cq.where(where);
        return entityManager.createQuery(cq).getSingleResult();
    }

}
