package com.ethan.gradation.manager;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceAsyncTaskExecutor;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 可被追踪的Listener
 */
public class TraceableRedisMessageListenerContainer extends RedisMessageListenerContainer {
    private final BeanFactory beanFactory;

    public TraceableRedisMessageListenerContainer(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    protected TaskExecutor createDefaultTaskExecutor() {
        AsyncTaskExecutor taskExecutor = (AsyncTaskExecutor) super.createDefaultTaskExecutor();
        return new LazyTraceAsyncTaskExecutor(beanFactory, taskExecutor);
    }
}
