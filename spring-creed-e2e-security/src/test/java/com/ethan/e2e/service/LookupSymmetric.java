package com.ethan.e2e.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@CacheConfig(cacheNames = "symmetric-cache")
public class LookupSymmetric {


  @Cacheable(key = "#uniqueKey", unless = "#result == null")
  public String getSymmetricKeyPair(String uniqueKey) {
    System.out.println("hit.....");
    return "ef882657e9eacf248917e978f967ccd476f2a6877513eea46b33c27bb11a2af16d495ea86601532c465ce6b99c28d7f0";
  }
}
