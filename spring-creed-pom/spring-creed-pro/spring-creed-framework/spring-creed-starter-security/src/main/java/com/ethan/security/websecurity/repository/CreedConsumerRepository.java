package com.ethan.security.websecurity.repository;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.security.websecurity.entity.CreedConsumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreedConsumerRepository extends JpaRepository<CreedConsumer, String>, JpaSpecificationExecutor<CreedConsumer> {
    // @Query("select s from CreedConsumer s where s.enabled = com.ethan.common.constant.CommonStatusEnum.ENABLE and s.username = ?1")
    Optional<CreedConsumer> findByUsername(String s);

    @Modifying
    @Query("update CreedConsumer s set s.enabled = com.ethan.common.constant.CommonStatusEnum.DISABLE where s.username = ?1")
    void deleteByUsername(String s);

    Optional<CreedConsumer> findByEmail(String email);

    Optional<CreedConsumer> findByPhone(String mobile);

    List<CreedConsumer> findByNickname(String nickname);

    List<CreedConsumer> findByUsernameContainingIgnoreCase(String nickname);

    List<CreedConsumer> findByEnabled(CommonStatusEnum status);
}
