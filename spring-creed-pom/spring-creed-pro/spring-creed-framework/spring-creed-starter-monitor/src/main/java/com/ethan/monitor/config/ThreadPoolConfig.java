package com.ethan.monitor.config;

import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * https://thelogiclooms.medium.com/how-to-get-ideal-thread-pool-size-9b2c0bb74906
 * Number of threads = Number of Available Cores * (1 + Wait time / Service time)
 *      - Wait time: Time spent waiting for IO-bound tasks to complete.
 *      - Service time: Time spent processing tasks.
 *      - Blocking coefficient: Wait time / Service time.
 *   core-size = 88    Number of Available Cores(8) * (1 + Wait time(500ms) / Service time(50ms))
 *   max-pool-size = coreSize * (2 ~ 4)
 *   queue-capacity = maxSize * (2 ~10)
 *
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor(){
            @Override
            protected ExecutorService initializeExecutor(ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
                ExecutorService executorService = super.initializeExecutor(threadFactory, rejectedExecutionHandler);
                return ContextExecutorService.wrap(executorService, ContextSnapshot::captureAll);
            }
            @Override
            public void execute(Runnable task) {
                super.execute(ContextSnapshot.captureAll().wrap(task));
            }

            @Override
            public Future<?> submit(Runnable task) {
                return super.submit(ContextSnapshot.captureAll().wrap(task));
            }

            @Override
            public <T> Future<T> submit(Callable<T> task) {
                return super.submit(ContextSnapshot.captureAll().wrap(task));
            }
        };

        taskExecutor.setCorePoolSize(80);
        taskExecutor.setMaxPoolSize(160);
        taskExecutor.setQueueCapacity(80);
        taskExecutor.setKeepAliveSeconds(2000);
        taskExecutor.setThreadNamePrefix("Asy-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        taskExecutor.initialize();
        return taskExecutor;
    }

}
