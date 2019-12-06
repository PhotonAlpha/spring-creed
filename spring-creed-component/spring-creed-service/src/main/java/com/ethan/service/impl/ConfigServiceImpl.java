package com.ethan.service.impl;

import com.ethan.dao.AppDictionaryDao;
import com.ethan.model.AppDictionary;
import com.ethan.service.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@CacheConfig(cacheNames = "short-term-cache")
public class ConfigServiceImpl implements ConfigService {
  @Autowired
  private AppDictionaryDao appDictionaryDao;

  @Cacheable(unless = "#result == null ")
  @Override
  public String play(Long appId, String type, String operator) {
    log.info("########################Executing: " + this.getClass().getSimpleName() + ".play(id:"+appId+";type:"+type+";operator:"+operator+");");
    return "Playing(id:"+appId+";type:"+type+";operator:"+operator+")";
  }

  @Override
  public List<String> put(String content) {
    log.info("########################Executing: %s .play(%s)", this.getClass().getSimpleName(), content);
    return Arrays.asList("playing " + content + "!");
  }

  @Override
  public Optional<AppDictionary> getOne(Long id) {
    return appDictionaryDao.findById(id);
  }

  @Override
  public List<AppDictionary> getAll() {
    return null;
  }

  @Override
  public boolean insertDic(AppDictionary entity) {
    return false;
  }

  @Override
  public boolean updateDic(AppDictionary entity) {
    return false;
  }

  @Override
  public boolean deleteDic(Long id) {
    return false;
  }
}