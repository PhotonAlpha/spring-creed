package com.ethan.example.service;

import com.ethan.example.controller.vo.ArtisanDetailsVO;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 1/4/25
 */
public interface ArtisanService {
    // @Cacheable with Sync does not support unless
    // {@see https://github.com/spring-projects/spring-framework/issues/20956}
    // @Cacheable(unless = "#result == null", key = "'EntityPermissionsV2_' + #id +'_'+ #name")
    ArtisanDetailsVO findByIdOrName(String id, String name);
}
