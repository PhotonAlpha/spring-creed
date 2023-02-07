package com.ethan.security.websecurity.repository;

import com.ethan.security.websecurity.entity.CreedGroupMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreedGroupsMembersRepository extends JpaRepository<CreedGroupMembers, String> {

    @Query(value = """
    select m from CreedGroupMembers m inner join m.groups g
    where g.groupname = :groupname and g.enabled = com.ethan.common.constant.CommonStatusEnum.ENABLE
    """)
    List<CreedGroupMembers> findUsersInGroup(String groupname);


}
