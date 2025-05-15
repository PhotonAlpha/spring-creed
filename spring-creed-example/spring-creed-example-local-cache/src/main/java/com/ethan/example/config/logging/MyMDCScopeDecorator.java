package com.ethan.example.config.logging;

import brave.baggage.BaggageField;
import brave.baggage.BaggageFields;
import brave.baggage.CorrelationScopeDecorator;
import brave.internal.CorrelationContext;
import brave.internal.Nullable;
import brave.propagation.CurrentTraceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;


/**
 * @author EthanCao
 * @description spring-creed
 * @date 17/4/25
 */
@Slf4j
public final class MyMDCScopeDecorator {
    static final CurrentTraceContext.ScopeDecorator INSTANCE = new MyMDCScopeDecorator.Builder().build();
    private static final String CORRELATION_TRACE_ID = "correlationTraceId";
    public static final BaggageField CORRELATION_FIELD = BaggageField.create(CORRELATION_TRACE_ID);

    /**
     * Returns a singleton that configures {@link BaggageFields#TRACE_ID} and {@link
     * BaggageFields#SPAN_ID}.
     *
     * @since 5.11
     */
    public static CurrentTraceContext.ScopeDecorator get() {
        return INSTANCE;
    }

    /**
     * Returns a builder that configures {@link BaggageFields#TRACE_ID} and {@link
     * BaggageFields#SPAN_ID}.
     *
     * @since 5.11
     */
    public static CorrelationScopeDecorator.Builder newBuilder() {
        return new MyMDCScopeDecorator.Builder();
    }

    static final class Builder extends CorrelationScopeDecorator.Builder {
        Builder() {
            super(MyMDCScopeDecorator.MDCContext.INSTANCE);
        }
    }

    enum MDCContext implements CorrelationContext {
        INSTANCE;

        @Override public String getValue(String name) {
            // if (StringUtils.equals(name, TRACE_ID)) {
            if (StringUtils.equals(name, CORRELATION_FIELD.name())) {
                //尝试获取CORRELATION_TRACE_ID,即自定义trace ID
                String correlationTraceId = CORRELATION_FIELD.getValue();
                if (StringUtils.isBlank(correlationTraceId)) {
                    return MDC.get(BaggageFields.TRACE_ID.name());
                } else {
                    update(BaggageFields.TRACE_ID.name(), correlationTraceId);
                    return correlationTraceId;
                }
            }
            return MDC.get(name);
        }

        @Override
        public boolean update(String name, @Nullable String value) {
            if (value != null) {
                MDC.put(name, value);
            } else {
                MDC.remove(name);
            }
            return true;
        }

    }
}
