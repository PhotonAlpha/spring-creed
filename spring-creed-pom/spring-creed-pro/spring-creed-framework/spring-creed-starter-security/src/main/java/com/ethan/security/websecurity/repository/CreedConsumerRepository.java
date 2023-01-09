package com.ethan.security.websecurity.repository;

import com.ethan.security.websecurity.entity.CreedConsumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreedConsumerRepository extends JpaRepository<CreedConsumer, String> {
    // @Query("select s from CreedConsumer s where s.enabled = com.ethan.common.constant.CommonStatusEnum.ENABLE and s.username = ?1")
    Optional<CreedConsumer> findByUsername(String s);

    @Modifying
    @Query("update CreedConsumer s set s.enabled = com.ethan.common.constant.CommonStatusEnum.DISABLE where s.username = ?1")
    void deleteByUsername(String s);

}
