package com.ethan.std.provisioning;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 3:09 PM
 */
@Repository
public interface Oauth2ClientDetailsRepository extends CrudRepository<Oauth2ClientDetails, String> {
    Optional<Oauth2ClientDetails> findByClientId(String clientId);
}
