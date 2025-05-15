package com.ethan.example;

import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 9/4/25
 */
@SpringBootTest(classes = LocalCacheApplication.class)
@Slf4j
public class TraceIdTest {
    @Resource
    Tracer tracer;

    // private static final Tracer tracer = Tracing.newBuilder().build().tracer();
    @Test
    void traceIdTest() {
        log.info("Using TraceId:");
        // log.info("Using TraceId: {}", tracer.currentSpan().context().traceIdString());
        // log.info("Using SpanId: {}", tracer.currentSpan().context().spanIdString());
    }
}
