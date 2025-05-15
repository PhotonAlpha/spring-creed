package com.ethan.example;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.baggage.BaggageField;
import brave.internal.baggage.BaggageFields;
import brave.propagation.CurrentTraceContext;
import brave.propagation.TraceContext;
import com.ethan.example.config.logging.MyMDCScopeDecorator;
import com.ethan.example.service.ArtisanService;
import com.ethan.example.service.ThreadDemoService;
import com.ethan.example.service.ThreadRetryService;
import io.micrometer.context.ContextExecutorService;
import io.micrometer.context.ContextSnapshotFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static brave.baggage.BaggageFields.TRACE_ID;
import static com.ethan.example.config.logging.MyMDCScopeDecorator.CORRELATION_FIELD;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 10/3/25
 */
@SpringBootTest(classes = LocalCacheApplication.class)
@Slf4j
public class ArtisanClientTest {
    @Resource
    ArtisanService artisanService;
    @Resource
    ThreadDemoService threadDemoService;

    @Test
    void testCache() throws InterruptedException {
        ExecutorService originalExecutorService = Executors.newFixedThreadPool(100);
        // environment setup
        ExecutorService executorService = ContextExecutorService.wrap(originalExecutorService, ContextSnapshotFactory.builder().build()::captureAll);

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            executorService.submit(() -> {
                // MDC.put("traceId", UUID.randomUUID().toString());
                try {
                    var result = artisanService.findByIdOrName(finalI +"", "xiaomi");
                    log.info("==>" + result);

                    var timeout = random.nextInt(30);
                    log.info("deply" + timeout);
                    TimeUnit.SECONDS.sleep(timeout);

                    result = artisanService.findByIdOrName(finalI +"", "xiaomi");
                    log.info("==>" + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        TimeUnit.SECONDS.sleep(31);
        executorService.shutdown();
    }

    public static final Random random = new Random();
    @Test
    void testCacheSync() throws InterruptedException {
        var result = artisanService.findByIdOrName("1", "xiaomi");
        var timeout = random.nextInt(10);
        System.out.println("deply" + timeout);
        System.out.println("result" + result);
        TimeUnit.SECONDS.sleep(timeout);

        result = artisanService.findByIdOrName("1", "xiaomi");
        timeout = random.nextInt(10);
        System.out.println("deply" + timeout);
        System.out.println("result" + result);
        TimeUnit.SECONDS.sleep(timeout);

        result = artisanService.findByIdOrName("1", "xiaomi");
        timeout = random.nextInt(10);
        System.out.println("deply" + timeout);
        System.out.println("result" + result);
        TimeUnit.SECONDS.sleep(timeout);

        result = artisanService.findByIdOrName("1", "xiaomi");
        timeout = random.nextInt(10);
        System.out.println("deply" + timeout);
        System.out.println("result" + result);
        TimeUnit.SECONDS.sleep(timeout);

        result = artisanService.findByIdOrName("1", "xiaomi");
        timeout = random.nextInt(10);
        System.out.println("deply" + timeout);
        System.out.println("result" + result);
        TimeUnit.SECONDS.sleep(timeout);

        result = artisanService.findByIdOrName("1", "xiaomi");
        System.out.println("result" + result);
        TimeUnit.SECONDS.sleep(30);
    }
    @Test
    void testCacheSync_no_sleep() throws InterruptedException {
        for (int i = 0; i < 12; i++) {
            var result = artisanService.findByIdOrName("1", "xiaomi");
            System.out.println("result" + result);
        }
    }

    @Test
    void threadPoolDemoTest() {
        try {
            var future1 = threadDemoService.getUsers(Thread.currentThread().getName());
            var future2 = threadDemoService.getUsersX(Thread.currentThread().getName());
            CompletableFuture.allOf(future1, future2).join();
            System.out.println(future1.get());
            System.out.println(future2.get());
        } catch (Exception e) {
            log.error("==>", e);
        }
    }

    @Resource
    ThreadRetryService threadRetryService;

    @Test
    void retryTest() {
        try {
            threadRetryService.sendReceive("a", "b", "c", (a) -> {
                System.out.println("a:" + a);
            });
        } catch (Exception e) {
            log.error("final", e);
        }

    }
}
