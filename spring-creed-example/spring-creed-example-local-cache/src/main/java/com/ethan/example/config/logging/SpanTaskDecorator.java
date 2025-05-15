package com.ethan.example.config.logging;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Nonnull;
import org.springframework.core.task.TaskDecorator;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 22/4/25
 */
public class SpanTaskDecorator implements TaskDecorator {
    private final Tracer tracer;

    public SpanTaskDecorator(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    @Nonnull
    public Runnable decorate(@Nonnull Runnable runnable) {
        return wrap(runnable);
    }

    public Runnable wrap(final Runnable runnable) {
        return () -> {
            Span span = tracer.nextSpan().name("child-thread-task").start();
            try (Tracer.SpanInScope ws = tracer.withSpan(span)){
                runnable.run();
            } finally {
                span.end();
            }
        };
    }

}
