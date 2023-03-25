/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.dept;

import com.ethan.system.dal.entity.dept.UserPostDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UserPostRepository extends JpaRepository<UserPostDO, Long> {

    List<UserPostDO> findByUserId(String userId);

    void deleteByUserIdAndPostIdIn(Long userId, Collection<Long> deletePostIds);

    void deleteByUserId(Long id);

    List<UserPostDO> findByPostIdIn(Collection<Long> postIds);
}
