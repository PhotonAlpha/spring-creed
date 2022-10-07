package com.ethan.std.provisioning;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/2/2022 5:10 PM
 */
@Repository
public interface UserProfileRepository extends CrudRepository<UserProfile, Long> {

    Optional<UserProfile> findByUsername(String username);

    @Query("update UserProfile u set u.accLocked = 1 where u.username = ?1")
    void deleteByName(String username);

    @Query("update UserProfile u set u.password = ?2 where u.username = ?1")
    void updatePassword(String username, String newPwd);

}
