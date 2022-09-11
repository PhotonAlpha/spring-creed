package com.ethan.std.provisioning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 3:12 PM
 */
@Repository
public interface OauthCodeRepository extends CrudRepository<OauthCode, String> {

}
