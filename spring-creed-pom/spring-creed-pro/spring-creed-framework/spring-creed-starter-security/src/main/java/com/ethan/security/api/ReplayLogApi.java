/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: caoqiang@dbs.com
 */

package com.ethan.security.api;


import com.ethan.security.api.dto.ReplayLogDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface ReplayLogApi {
    Optional<ReplayLogDto> findByNonce(String token, String nonce, String timestamp);
    Optional<ReplayLogDto> findByNonce(String token, String nonce);

    void save(ReplayLogDto depCaseReplayLog);

    List<ReplayLogDto> findByCreateTimeLessThan(ZonedDateTime instant);

    void deleteAll(List<ReplayLogDto> list);
}
