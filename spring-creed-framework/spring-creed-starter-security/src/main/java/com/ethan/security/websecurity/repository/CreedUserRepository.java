package com.ethan.security.websecurity.repository;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.security.websecurity.entity.CreedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Deprecated(forRemoval = true)
public interface CreedUserRepository extends JpaRepository<CreedUser, String>, JpaSpecificationExecutor<CreedUser> {
    // @Query("select s from CreedConsumer s where s.enabled = com.ethan.common.constant.CommonStatusEnum.ENABLE and s.username = ?1")
    Optional<CreedUser> findByUsername(String s);

    List<CreedUser> findByAuthoritiesIdIn(Collection<String> s);

    @Modifying
    @Query("update CreedUser s set s.enabled = com.ethan.common.constant.CommonStatusEnum.DISABLE where s.username = ?1")
    void deleteByUsername(String s);

    Optional<CreedUser> findByEmail(String email);

    Optional<CreedUser> findByPhone(String mobile);

    List<CreedUser> findByNickname(String nickname);

    List<CreedUser> findByUsernameContainingIgnoreCase(String nickname);

    List<CreedUser> findByEnabled(CommonStatusEnum status);

    long countByUpdateTimeGreaterThan(ZonedDateTime maxUpdateTime);
}
