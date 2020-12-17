package com.ethan.auth.dao;

import com.ethan.auth.domain.OauthClientDetailsDO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthClientDetailsDao extends JpaRepository<OauthClientDetailsDO, Long> {
}
