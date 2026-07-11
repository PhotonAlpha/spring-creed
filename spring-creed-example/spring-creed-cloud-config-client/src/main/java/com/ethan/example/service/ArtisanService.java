package com.ethan.example.service;

import com.ethan.example.controller.vo.ArtisanDetailsVO;

import java.util.List;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 1/4/25
 */
public interface ArtisanService {
    ArtisanDetailsVO findByIdOrName(String id, String name);

    List<ArtisanDetailsVO> findAll();

    List<ArtisanDetailsVO> findAllRemote();
}
