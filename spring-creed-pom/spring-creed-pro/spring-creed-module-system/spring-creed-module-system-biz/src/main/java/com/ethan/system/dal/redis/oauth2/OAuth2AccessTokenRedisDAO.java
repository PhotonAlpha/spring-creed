package com.ethan.system.dal.redis.oauth2;

import com.ethan.common.utils.collection.CollUtils;
import com.ethan.common.utils.json.JacksonUtils;
import com.ethan.security.oauth2.entity.client.CreedOAuth2AuthorizedClient;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ethan.system.dal.redis.core.RedisKeyConstants.OAUTH2_ACCESS_TOKEN;

/**
 * {@link CreedOAuth2AuthorizedClient} 的 RedisDAO
 *
 * 
 */
@Repository
public class OAuth2AccessTokenRedisDAO {
    public static final Map<String, String> stringRedisTemplate = new HashMap<>();
    /* @Resource
    private StringRedisTemplate stringRedisTemplate;
 */
    public CreedOAuth2AuthorizedClient get(String accessToken) {
        String redisKey = formatKey(accessToken);
        return JacksonUtils.parseObject(stringRedisTemplate.get(redisKey), CreedOAuth2AuthorizedClient.class);
    }

    public void set(CreedOAuth2AuthorizedClient accessTokenDO) {
        String redisKey = formatKey(accessTokenDO.getAccessTokenValue());
        // 清理多余字段，避免缓存
        // accessTokenDO.setUpda(null).setUpdateTime(null).setCreateTime(null).setCreator(null).setEnabled(null);
        // stringRedisTemplate.put(redisKey, JacksonUtils.toJsonString(accessTokenDO),
        //         accessTokenDO.getExpiresTime().getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        stringRedisTemplate.put(redisKey, JacksonUtils.toJsonString(accessTokenDO));
    }

    public void delete(String accessToken) {
        String redisKey = formatKey(accessToken);
        stringRedisTemplate.remove(redisKey);
    }

    public void deleteList(Collection<String> accessTokens) {
        List<String> redisKeys = CollUtils.convertList(accessTokens, OAuth2AccessTokenRedisDAO::formatKey);
        stringRedisTemplate.remove(redisKeys);
    }

    private static String formatKey(String accessToken) {
        return String.format(OAUTH2_ACCESS_TOKEN.getKeyTemplate(), accessToken);
    }

}
