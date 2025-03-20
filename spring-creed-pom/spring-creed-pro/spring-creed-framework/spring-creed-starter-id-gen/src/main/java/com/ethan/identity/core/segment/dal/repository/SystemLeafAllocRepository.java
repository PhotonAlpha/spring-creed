package com.ethan.identity.core.segment.dal.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import com.ethan.identity.core.segment.dal.entity.SystemLeafAlloc;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 30/12/24
 */
@Repository
public interface SystemLeafAllocRepository extends JpaRepository<SystemLeafAlloc, String> {
    @Query("SELECT new com.ethan.identity.core.segment.dal.entity.SystemLeafAlloc(bizTag, maxId, step) FROM SystemLeafAlloc WHERE bizTag = :tag")
    Optional<SystemLeafAlloc> findByBizTag(@Param("tag") String tag);

    @Modifying
    @Query("UPDATE SystemLeafAlloc SET maxId = maxId + step WHERE bizTag = :tag")
    void updateMaxId(@Param("tag") String tag);

    @Modifying
    @Query("UPDATE SystemLeafAlloc SET maxId = maxId + :#{#leafAlloc.step} WHERE bizTag = :#{#leafAlloc.bizTag}")
    void updateMaxIdByCustomStep(@Param("leafAlloc") SystemLeafAlloc leafAlloc);

    @Query("SELECT bizTag FROM SystemLeafAlloc")
    List<String> getAllTags();

    @Modifying
    @Query(value = "DELETE FROM SystemLeafAlloc WHERE bizTag = :tag")
    void deleteSystemLeafAlloc(@Param("tag") String tag);
}
