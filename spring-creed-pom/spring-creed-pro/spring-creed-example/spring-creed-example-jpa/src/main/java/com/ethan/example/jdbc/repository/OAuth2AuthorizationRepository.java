package com.ethan.example.jdbc.repository;

import com.ethan.example.jdbc.dal.permission.CreedOAuth2AuthorizationVO;
import org.springframework.data.domain.Example;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 25/2/25
 */
public interface OAuth2AuthorizationRepository extends ListPagingAndSortingRepository<CreedOAuth2AuthorizationVO, String>, QueryByExampleExecutor<CreedOAuth2AuthorizationVO> {
    @Query("""
        select cli.client_id,
               az.id,
               az.registered_client_id,
               az.access_token_value,
               az.access_token_issued_at,
               az.principal_name,
               az.refresh_token_value,
               az.refresh_token_issued_at
        from creed_oauth2_authorization az
         inner join creed_oauth2_registered_client cli on az.registered_client_id = cli.id
    """)
    List<CreedOAuth2AuthorizationVO> findByCondition(Example<CreedOAuth2AuthorizationVO> example);

    @Query("""
        select az.id,
               az.registered_client_id,
               az.access_token_value,
               az.access_token_issued_at,
               az.principal_name,
               az.refresh_token_value,
               az.refresh_token_issued_at
        from creed_oauth2_authorization az where az.principal_name = :name
    """)
    List<CreedOAuth2AuthorizationVO> findByPrincipalName(String name);
}
