package com.ethan.gradation.manager;

import com.ethan.gradation.config.GradationCacheProperty;
import com.ethan.gradation.listener.RedisMessageListener;
import com.ethan.gradation.stats.CacheStatsInfo;
import com.ethan.gradation.stats.StatsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 公共的抽象 {@link CacheManager} 的实现.
 *
 * @author yuhao.wang3
 */
@Slf4j
public abstract class AbstractCacheManagerGdt implements CacheManager, InitializingBean, DisposableBean, BeanNameAware, SmartLifecycle, ApplicationContextAware {
    /**
     * CacheManager 容器
     */
    static Set<AbstractCacheManagerGdt> cacheManagers = new LinkedHashSet<>();
    /**
     * redis pub/sub 容器
     */
    private final RedisMessageListenerContainer container = new RedisMessageListenerContainer();

    /**
     * redis pub/sub 监听器
     */
    private final RedisMessageListener messageListener = new RedisMessageListener();

    /**
     * 缓存容器
     */
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);

    /**
     * 缓存名称容器
     */
    private volatile Set<String> cacheNames = Collections.emptySet();

    /**
     * 是否开启统计
     */
    private boolean stats = false;

    /**
     * redis 客户端
     */
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * GradationCacheProperty gradationCacheProperty
     *
     * @return
     */
    private GradationCacheProperty gradationCacheProperty;

    private ApplicationContext applicationContext;

    protected AbstractCacheManagerGdt(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public AbstractCacheManagerGdt(RedisTemplate<String, Object> redisTemplate, GradationCacheProperty gradationCacheProperty) {
        Assert.notNull(gradationCacheProperty, "GradationCacheProperty can not null");
        this.redisTemplate = redisTemplate;
        this.gradationCacheProperty = gradationCacheProperty;
    }

    @Override
    public void afterPropertiesSet() {
        messageListener.setCacheManager(this);
        container.setConnectionFactory(getRedisTemplate().getConnectionFactory());
        container.afterPropertiesSet();
        messageListener.afterPropertiesSet();

        applicationContext.getBean(StatsService.class).setCacheManager(this);
        if (getStats()) {
            // 采集缓存命中率数据
            applicationContext.getBean(StatsService.class).syncCacheStats();
        }

        // 初始化缓存列表
        Collection<? extends Cache> caches = loadCaches();
        synchronized (this.cacheMap) {
            this.cacheNames = Collections.emptySet();
            this.cacheMap.clear();
            Set<String> cacheNames = new LinkedHashSet<>(caches.size());
            for (Cache cache : caches) {
                String name = cache.getName();
                this.cacheMap.put(name, decorateCache(cache));
                cacheNames.add(name);
            }
            this.cacheNames = Collections.unmodifiableSet(cacheNames);
        }
    }

    /**
     * Load the initial caches for this cache manager.
     * <p>Called by {@link #afterPropertiesSet()} on startup.
     * The returned collection may be empty but must not be {@code null}.
     */
    protected abstract Collection<? extends Cache> loadCaches();

    @Override
    @Nullable
    public Cache getCache(String name) {
        // 第一次获取缓存Cache，如果有直接返回,如果没有加锁往容器里里面放Cache
        Cache cache = this.cacheMap.get(name);
        if (cache != null) {
            return cache;
        }

        // 第二次获取缓存Cache，加锁往容器里里面放Cache
        Cache missingCache = getMissingCache(name);
        if (missingCache != null) {
            synchronized (this.cacheMap) {
                cache = this.cacheMap.get(name);
                if (cache == null) {
                    cache = decorateCache(missingCache);
                    this.cacheMap.put(name, cache);
                    // 更新缓存名称
                    updateCacheNames(name);
                    // 创建redis监听
                    addMessageListener(name);
                }
            }
        }
        return cache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.cacheNames;
    }

    @Nullable
    protected final Cache lookupCache(String name) {
        return this.cacheMap.get(name);
    }

    /**
     * 更新缓存名称容器
     *
     * @param name 需要添加的缓存名称
     */
    private void updateCacheNames(String name) {
        Set<String> cacheNames = new LinkedHashSet<>(this.cacheNames.size() + 1);
        cacheNames.addAll(this.cacheNames);
        cacheNames.add(name);
        this.cacheNames = Collections.unmodifiableSet(cacheNames);
    }


    /**
     * 获取Cache对象的装饰示例
     *
     * @param cache 需要添加到CacheManager的Cache实例
     * @return 装饰过后的Cache实例
     */
    protected Cache decorateCache(Cache cache) {
        return cache;
    }

    /**
     * 根据缓存名称在CacheManager中没有找到对应Cache时，通过该方法新建一个对应的Cache实例
     *
     * @param name                 缓存名称
     * @return {@link Cache}
     */
    protected abstract Cache getMissingCache(String name);

    /**
     * 添加消息监听
     *
     * @param name 缓存名称
     */
    protected void addMessageListener(String name) {
        container.addMessageListener(messageListener, new ChannelTopic(name));
    }

    public List<CacheStatsInfo> listCacheStats(String cacheName) {
        return applicationContext.getBean(StatsService.class).listCacheStats(cacheName);
    }

    public void resetCacheStat() {
        applicationContext.getBean(StatsService.class).resetCacheStat();
    }

    @Override
    public void setBeanName(String name) {
        container.setBeanName("redisMessageListenerContainer");
    }

    @Override
    public void destroy() throws Exception {
        container.destroy();
        applicationContext.getBean(StatsService.class).shutdownExecutor();
    }

    @Override
    public boolean isAutoStartup() {
        return container.isAutoStartup();
    }

    @Override
    public void stop(Runnable callback) {
        container.stop(callback);
    }

    @Override
    public void start() {
        container.start();
    }

    @Override
    public void stop() {
        container.stop();
    }

    @Override
    public boolean isRunning() {
        return container.isRunning();
    }

    @Override
    public int getPhase() {
        return container.getPhase();
    }

    public boolean getStats() {
        return stats;
    }

    public void setStats(boolean stats) {
        this.stats = stats;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public static Set<AbstractCacheManagerGdt> getCacheManagers() {
        return cacheManagers;
    }
}
