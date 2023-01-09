package com.ethan.security.websecurity.repository;

import com.ethan.common.constant.CommonStatusEnum;
import com.ethan.security.websecurity.entity.CreedGroups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreedGroupsRepository extends JpaRepository<CreedGroups, String> {
    Optional<CreedGroups> findByGroupname(String s);

    List<CreedGroups> findByEnabled(CommonStatusEnum enabled);

    default List<CreedGroups>  findAllByEnabled() {
        return findByEnabled(CommonStatusEnum.ENABLE);
    }
}
