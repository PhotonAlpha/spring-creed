package com.ethan.app.dao;

import com.ethan.entity.RoleDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleDao extends JpaRepository<RoleDO, Long> {
}
