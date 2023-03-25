/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.dal.repository.dept;

import com.ethan.system.dal.entity.dept.PostDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<PostDO, Long>, JpaSpecificationExecutor<PostDO> {

    List<PostDO> findByIdInAndStatusIn(Collection<Long> ids, Collection<Integer> statuses);

    Optional<PostDO> findByName(String name);

    Optional<PostDO> findByCode(String code);
}
