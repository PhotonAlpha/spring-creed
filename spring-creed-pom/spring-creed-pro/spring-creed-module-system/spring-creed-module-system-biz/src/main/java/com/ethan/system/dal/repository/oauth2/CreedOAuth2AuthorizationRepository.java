package com.ethan.system.dal.repository.oauth2;


import com.ethan.common.common.R;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2Authorization;
import com.ethan.system.dal.entity.oauth2.CreedOAuth2AuthorizationVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public interface CreedOAuth2AuthorizationRepository extends JpaRepository<CreedOAuth2Authorization, String>, JpaSpecificationExecutor<CreedOAuth2Authorization> {

    Optional<CreedOAuth2Authorization> findByState(String s);

    Optional<CreedOAuth2Authorization> findByAuthorizationCodeValue(String authorizationCode);
    Optional<CreedOAuth2Authorization> findByAccessTokenValue(String accessToken);

    Optional<CreedOAuth2Authorization> findByRefreshTokenValue(String refreshToken);

    Optional<CreedOAuth2Authorization> findByOidcIdTokenValue(String idToken);
    Optional<CreedOAuth2Authorization> findByUserCodeValue(String userCode);
    Optional<CreedOAuth2Authorization> findByDeviceCodeValue(String deviceCode);

    @Query("select a from CreedOAuth2Authorization a where a.state = :token" +
            " or a.authorizationCodeValue = :token" +
            " or a.accessTokenValue = :token" +
            " or a.refreshTokenValue = :token" +
            " or a.oidcIdTokenValue = :token" +
            " or a.userCodeValue = :token" +
            " or a.deviceCodeValue = :token"
    )
    Optional<CreedOAuth2Authorization> findByTokenValueOrCodeValue(@Param("token") String token);

    Optional<CreedOAuth2Authorization> findByRegisteredClientIdAndPrincipalName(String registeredClientId, String principalName);

    Page<CreedOAuth2AuthorizationVO> findBy(Specification<CreedOAuth2AuthorizationVO> specification, Function<FluentQuery.FetchableFluentQuery<CreedOAuth2AuthorizationVO>, CreedOAuth2AuthorizationVO> queryFunction, Pageable pageable);


    /* default List<CreedOAuth2AuthorizationVO> findByCondition(OAuth2AccessTokenPageReqVO reqVO) {
        Specification<CreedOAuth2AuthorizationVO> creedOAuth2AuthorizationSpecification = (Specification<CreedOAuth2AuthorizationVO>) (root, query, cb) -> {
            CriteriaQuery<CreedOAuth2AuthorizationVO> newQuery = cb.createQuery(CreedOAuth2AuthorizationVO.class);
            Root<CreedOAuth2Authorization> authRoot = newQuery.from(CreedOAuth2Authorization.class);
            Root<CreedOAuth2RegisteredClient> clientRoot = newQuery.from(CreedOAuth2RegisteredClient.class);
            Predicate joinCondition = cb.equal(authRoot.get("registeredClientId"), clientRoot.get("id"));
            List<Predicate> predicateList = new ArrayList<>();
            predicateList.add(joinCondition);
            if (StringUtils.isNotBlank(reqVO.getUserName())) {
                // 本处都转为小写，进行模糊匹配
                predicateList.add(cb.like(authRoot.get("principalName"), "%" + reqVO.getUserName() + "%"));
            }
            if (StringUtils.isNotBlank(reqVO.getClientId())) {
                predicateList.add(cb.equal(clientRoot.get("clientId"), reqVO.getClientId()));
            }
            query.orderBy(cb.desc(authRoot.get("id")));
            Selection<CreedOAuth2AuthorizationVO> registeredClientId = cb.construct(CreedOAuth2AuthorizationVO.class, authRoot.get("registeredClientId"));
            newQuery.select(registeredClientId);
            newQuery.where(predicateList.toArray(new Predicate[0]));
            return null;
        };
        // return findAll();

    } */
}
