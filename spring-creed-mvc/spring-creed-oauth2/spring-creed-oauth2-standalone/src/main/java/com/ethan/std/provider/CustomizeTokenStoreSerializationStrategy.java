package com.ethan.std.provider;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.provider.token.store.redis.BaseRedisTokenStoreSerializationStrategy;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/8/2022 5:59 PM
 */
public class CustomizeTokenStoreSerializationStrategy extends BaseRedisTokenStoreSerializationStrategy {

    private RedisSerializer<Object> objectSerializer = new JdkSerializationRedisSerializer();
    private RedisSerializer<String> stringSerializer = new StringRedisSerializer();

    @Override
    protected String deserializeStringInternal(byte[] bytes) {
        return stringSerializer.deserialize(bytes);
    }

    @Override
    protected byte[] serializeInternal(String string) {
        return stringSerializer.serialize(string);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
        return (T) objectSerializer.deserialize(bytes);
    }

    @Override
    protected byte[] serializeInternal(Object object) {
        return objectSerializer.serialize(object);
    }

    public void setObjectSerializer(RedisSerializer<Object> objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    public void setStringSerializer(RedisSerializer<String> stringSerializer) {
        this.stringSerializer = stringSerializer;
    }
}
