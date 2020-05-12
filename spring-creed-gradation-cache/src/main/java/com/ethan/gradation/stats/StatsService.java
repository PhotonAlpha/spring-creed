package com.ethan.gradation.stats;

import com.ethan.gradation.cache.GradationCache;
import com.ethan.gradation.config.GradationCacheProperty;
import com.ethan.gradation.manager.AbstractCacheManagerGdt;
import com.ethan.gradation.support.RedisLock;
import com.ethan.gradation.util.RedisHelper;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * 统计服务
 *
 */
@Slf4j
@Component
public class StatsService {
    /**
     * 缓存统计数据前缀
     */
    public static final String CACHE_STATS_KEY_PREFIX = "gradation-cache::cache_stats_info::";

    /**
     * 定时任务线程池
     */
    private static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(50);

    /**
     * {@link AbstractCacheManagerGdt }
     */
    private AbstractCacheManagerGdt cacheManager;

    /**
     * 获取缓存统计list
     *
     * @param cacheNameParam 缓存名称
     * @return List&lt;CacheStatsInfo&gt;
     */
    public List<CacheStatsInfo> listCacheStats(String cacheNameParam) {
        log.info("获取缓存统计数据");

        Set<String> gradationCacheKeys = RedisHelper.scan(cacheManager.getRedisTemplate(), CACHE_STATS_KEY_PREFIX + "*");
        if (CollectionUtils.isEmpty(gradationCacheKeys)) {
            return Collections.emptyList();
        }
        // 遍历找出对应统计数据
        List<CacheStatsInfo> statsList = new ArrayList<>();
        for (String key : gradationCacheKeys) {
            if (StringUtils.isNotBlank(cacheNameParam) && !key.startsWith(CACHE_STATS_KEY_PREFIX + cacheNameParam)) {
                continue;
            }

            CacheStatsInfo cacheStats = (CacheStatsInfo) cacheManager.getRedisTemplate().opsForValue().get(key);
            if (!Objects.isNull(cacheStats)) {
                statsList.add(cacheStats);
            }
        }

        return statsList.stream().sorted(Comparator.comparing(CacheStatsInfo::getHitRate)).collect(Collectors.toList());
    }

    /**
     * 同步缓存统计list
     * TODO
     */
    public void syncCacheStats() {
        RedisTemplate<String, Object> redisTemplate = cacheManager.getRedisTemplate();
        // 清空统计数据
        resetCacheStat();
        executor.scheduleWithFixedDelay(() -> {
            log.debug("执行缓存统计数据采集定时任务");
            Set<AbstractCacheManagerGdt> cacheManagers = AbstractCacheManagerGdt.getCacheManagers();
            for (AbstractCacheManagerGdt abstractCacheManager : cacheManagers) {
                // 获取CacheManager
                CacheManager cacheManager = abstractCacheManager;
                Collection<String> cacheNames = cacheManager.getCacheNames();
                for (String cacheName : cacheNames) {
                    // 获取Cache
                    GradationCache cache = (GradationCache) cacheManager.getCache(cacheName);
                    GradationCacheProperty prop = cache.getGradationCacheProperty();
                    // 加锁并增量缓存统计数据，缓存key=固定前缀 +缓存名称加
                    String redisKey = CACHE_STATS_KEY_PREFIX + cacheName;
                    RedisLock lock = new RedisLock(redisTemplate, redisKey, 60, 5000);
                    try {
                        if (lock.tryLock()) {
                            CacheStatsInfo cacheStats = (CacheStatsInfo) redisTemplate.opsForValue().get(redisKey);
                            if (Objects.isNull(cacheStats)) {
                                cacheStats = new CacheStatsInfo();
                            }

                            // 设置缓存唯一标示
                            cacheStats.setCacheName(cacheName);
                            cacheStats.setInternalKey(redisKey);

                            cacheStats.setDepict(prop.getDepict());
                            // 设置缓存配置信息
                            cacheStats.setGradationCacheProperty(prop);

                            // 设置缓存统计数据
                            CacheStats gradationCacheStats = cache.getCacheStats();
                            CacheStats firstCacheStats = cache.getFirstCache().getCacheStats();
                            CacheStats secondCacheStats = cache.getSecondCache().getCacheStats();
                            log.info("gradationCacheStats:{}", gradationCacheStats);
                            log.info("firstCacheStats:{}", firstCacheStats);
                            log.info("secondCacheStats:{}", secondCacheStats);

                            // 清空加载缓存时间
                            cacheStats.setRequestCount(gradationCacheStats.requestCount());
                            cacheStats.setMissCount(gradationCacheStats.missCount());
                            cacheStats.setTotalLoadTime(gradationCacheStats.totalLoadTime());
                            cacheStats.setHitRate((gradationCacheStats.hitRate()));

                            cacheStats.setFirstCacheRequestCount(firstCacheStats.requestCount());
                            cacheStats.setFirstCacheMissCount(firstCacheStats.missCount());

                            cacheStats.setSecondCacheRequestCount(secondCacheStats.requestCount());
                            cacheStats.setSecondCacheMissCount(secondCacheStats.missCount());

                            // 将缓存统计数据写到redis
                            redisTemplate.opsForValue().set(redisKey, cacheStats, 24, TimeUnit.HOURS);

                            log.info("Layering Cache 统计信息：{}", cacheStats);
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    } finally {
                        lock.unlock();
                    }
                }
            }
            //  初始时间间隔是1分
        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 关闭线程池
     */
    public void shutdownExecutor() {
        executor.shutdown();
    }

    /**
     * 重置缓存统计数据
     */
    public void resetCacheStat() {
        RedisTemplate<String, Object> redisTemplate = cacheManager.getRedisTemplate();
        Set<String> gradationCacheKeys = RedisHelper.scan(redisTemplate, CACHE_STATS_KEY_PREFIX + "*");

        for (String key : gradationCacheKeys) {
            resetCacheStat(key);
        }
    }

    /**
     * 重置缓存统计数据
     *
     * @param redisKey redisKey
     */
    public void resetCacheStat(String redisKey) {
        RedisTemplate<String, Object> redisTemplate = cacheManager.getRedisTemplate();
        CacheStatsInfo cacheStats = (CacheStatsInfo) redisTemplate.opsForValue().get(redisKey);
        if (Objects.nonNull(cacheStats)) {
            cacheStats.clearStatsInfo();
            // 将缓存统计数据写到redis
            redisTemplate.opsForValue().set(redisKey, cacheStats, 24, TimeUnit.HOURS);
        }
    }

    public void setCacheManager(AbstractCacheManagerGdt cacheManager) {
        this.cacheManager = cacheManager;
    }
}
