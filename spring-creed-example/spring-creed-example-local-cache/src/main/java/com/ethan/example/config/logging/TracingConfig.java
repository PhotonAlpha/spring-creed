package com.ethan.example.config.logging;

import brave.baggage.*;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 17/4/25
 */
@Configuration
public class TracingConfig {
    /**
     * 参考 {@link BaggageField } 上的注释，添加自定义 BaggageField
     * 参考 {@link BaggageFields#TRACE_ID }
     * @param correlationScopeCustomizers
     * @return
     */
    @Bean
    CorrelationScopeDecorator.Builder mdcCorrelationScopeDecoratorBuilder(
            ObjectProvider<CorrelationScopeCustomizer> correlationScopeCustomizers) {
        CorrelationScopeDecorator.Builder builder = MyMDCScopeDecorator.newBuilder();
        builder.add(CorrelationScopeConfig.SingleCorrelationField.create(MyMDCScopeDecorator.CORRELATION_FIELD));
        correlationScopeCustomizers.orderedStream().forEach((customizer) ->
                customizer
                .customize(builder));

        return builder;
    }
    @Bean
    public BaggagePropagationCustomizer baggagePropagationCustomizer() {
        return builder -> {
            builder.add(BaggagePropagationConfig.SingleBaggageField.remote(MyMDCScopeDecorator.CORRELATION_FIELD));
        };

    }
}
