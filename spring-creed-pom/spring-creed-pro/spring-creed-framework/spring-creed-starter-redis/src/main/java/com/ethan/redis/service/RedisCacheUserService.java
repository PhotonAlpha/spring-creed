/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 指定默认缓存区
 * 缓存区：key的前缀，与指定的key构成redis的key，如 user::10001
 */
@Slf4j
@CacheConfig(cacheNames = "user")
@Service
public class RedisCacheUserService {
    public static final Map<String, User> STORE = new HashMap<>();
    /**
     * @Cacheable 缓存有数据时，从缓存获取；没有数据时，执行方法，并将返回值保存到缓存中
     * @Cacheable 一般在查询中使用
     * 1) cacheNames 指定缓存区，没有配置使用@CacheConfig指定的缓存区
     * 2) key 指定缓存区的key
     * 3) 注解的值使用SpEL表达式
     * eq ==
     * lt <
     * le <=
     * gt >
     * ge >=
     */
    @Cacheable(cacheNames = "user", key = "#id")
    public User selectUserById(String id) {
        log.info("##hit selectUserById##{}", id);
        return STORE.get(id);
    }
    @Cacheable(key="'list'")
    public List<User> selectUser() {
        log.info("##hit selectUser##");
        /**
         * {@link https://github.com/FasterXML/jackson-databind/issues/3344}
         * Jackson  暂时不支持 toList(), 序列化会丢失list类型
         */
        // return STORE.values().stream().toList();
        return STORE.values().stream().collect(Collectors.toList());
    }
    /**
     * condition 满足条件缓存数据
     */
    @Cacheable(key = "#id", condition = "#number ge 20") // >= 20
    public User selectUserByIdWithCondition(String id, int number) {
        log.info("##hit selectUserByIdWithCondition##{}-{}", id, number);
        return STORE.get(id);
    }
    /**
     * unless 满足条件时否决缓存数据
     */
    @Cacheable(key = "#id", unless = "#number lt 20") // < 20
    public User selectUserByIdWithUnless(String id, int number) {
        log.info("##hit selectUserByIdWithUnless##{}-{}", id, number);
        return STORE.get(id);
    }
    /**
     　　　* @CachePut 一定会执行方法，并将返回值保存到缓存中
     * @CachePut 一般在新增和修改中使用
     */
    @CachePut(key = "#user.id")
    public User insertUser(User user) {
        log.info("##hit insertUser##{}", user);
        STORE.put(user.getId(), user);
        return user;
    }
    @CachePut(key = "#user.id", condition = "#user.age ge 20")
    public User insertUserWithCondition(User user) {
        log.info("##hit insertUserWithCondition##{}", user);
        STORE.put(user.getId(), user);
        return user;
    }
    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        log.info("##hit updateUser##{}", user);
        STORE.put(user.getId(), user);
        return user;
    }
    /**
     * 根据key删除缓存区中的数据
     */
    @CacheEvict(key = "#id")
    public void deleteUserById(String id) {
        log.info("##hit deleteUserById##{}", id);
        STORE.remove(id);
    }
    /**
     * allEntries = true ：删除整个缓存区的所有值，此时指定的key无效
     * beforeInvocation = true ：默认false，表示调用方法之后删除缓存数据；true时，在调用之前删除缓存数据(如方法出现异常)
     */
    @CacheEvict(key = "#id", allEntries = true)
    public void deleteUserByIdAndCleanCache(String id) {
        log.info("##hit deleteUserByIdAndCleanCache##{}", id);
        STORE.remove(id);
    }
}
