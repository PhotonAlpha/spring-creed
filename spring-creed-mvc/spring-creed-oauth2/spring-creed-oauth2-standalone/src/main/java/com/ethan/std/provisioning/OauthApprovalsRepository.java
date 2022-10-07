package com.ethan.std.provisioning;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/4/2022 3:11 PM
 */
@Repository
public interface OauthApprovalsRepository extends CrudRepository<OauthApprovals, String> {

}
