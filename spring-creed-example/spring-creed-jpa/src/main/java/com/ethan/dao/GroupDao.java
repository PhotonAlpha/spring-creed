package com.ethan.dao;

import com.ethan.entity.GroupDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupDao extends JpaRepository<GroupDO, Long> {
}
