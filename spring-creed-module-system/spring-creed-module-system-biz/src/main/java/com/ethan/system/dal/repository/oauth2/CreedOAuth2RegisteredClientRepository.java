package com.ethan.system.dal.repository.oauth2;

import com.ethan.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2RegisteredClient;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreedOAuth2RegisteredClientRepository extends JpaRepository<CreedOAuth2RegisteredClient, String>, JpaSpecificationExecutor<CreedOAuth2RegisteredClient> {

    Optional<CreedOAuth2RegisteredClient> findByClientId(String clientId);
    boolean existsByClientId(String clientId);

    boolean existsById(String id);


    /**
     * 需求 1. join 2. 带条件查询 3. 分页查询
     *
     * @param reqVO
     * @return
     */
    default Page<CreedOAuth2RegisteredClient> findByCondition(OAuth2ClientPageReqVO reqVO, Pageable pageable) {
        return findAll(getCreedOAuth2RegisteredClientSpecification(reqVO), pageable);
    }

    default List<CreedOAuth2RegisteredClient> findByAllCondition(OAuth2ClientPageReqVO reqVO) {
        return findAll(getCreedOAuth2RegisteredClientSpecification(reqVO));
    }

    @NotNull
    private static Specification<CreedOAuth2RegisteredClient> getCreedOAuth2RegisteredClientSpecification(OAuth2ClientPageReqVO reqVO) {
        Specification<CreedOAuth2RegisteredClient> spec = (root, query, cb) -> {
            List<Predicate> predicateList = new ArrayList<>();
            if (StringUtils.isNotBlank(reqVO.getName())) {
                // 本处都转为小写，进行模糊匹配
                predicateList.add(cb.like(cb.lower(root.get("clientId")), "%" + StringUtils.lowerCase(reqVO.getName()) + "%"));
            }
            query.orderBy(cb.desc(root.get("id")));
            return cb.and(predicateList.toArray(new Predicate[0]));
        };
        return spec;
    }
}
