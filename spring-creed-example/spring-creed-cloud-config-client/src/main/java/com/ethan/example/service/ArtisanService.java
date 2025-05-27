package com.ethan.example.service;

import com.ethan.example.controller.vo.ArtisanDetailsVO;
import io.github.resilience4j.retry.annotation.Retry;

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
