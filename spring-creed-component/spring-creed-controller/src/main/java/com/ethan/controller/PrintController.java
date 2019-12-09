package com.ethan.controller;

import com.ethan.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping("api/test")
public class PrintController extends BasicController {
  @Autowired
  private ConfigService configService;
  @Autowired
  private CacheManager cacheManager;

  @EventListener(ApplicationReadyEvent.class)
  public void initialCache() {
    for (int i = 0; i < 10; i++) {
      configService.play(1L, "dic" + i, "maker");
    }
  }

  @GetMapping(value = "/content/{name}")
  public ResponseEntity<List> getContent(@PathVariable("name") String name) {
    return ResponseEntity.ok(configService.put(name));
  }
  @GetMapping(value = "/play")
  public ResponseEntity<String> getContent() {
    return ResponseEntity.ok(configService.play(1L, "dic", "maker"));
  }

  @GetMapping(value = "/play/evict")
  public ResponseEntity<String> evictContent() {
    Cache cacheMan = cacheManager.getCache("short-term-cache");
    ConcurrentMap<Object, Object> cacheMaps = ((CaffeineCache) cacheMan).getNativeCache().asMap();
    int previousAmount = cacheMaps.size();
    cacheMaps.keySet().stream().filter(cacheKey -> cacheKey instanceof String).filter(cacheKey -> ((String) cacheKey).contains("dic"))
        .forEach(cacheKey -> {
          log.info("cacheKey:=={}==evicted.", cacheKey);
          cacheMan.evict(cacheKey);
        });
    int afterAmount = cacheMaps.size();
    System.out.println("afterAmount:"+afterAmount);
    return ResponseEntity.ok(configService.playEvict(1L, "dic", "maker"));
  }
}
