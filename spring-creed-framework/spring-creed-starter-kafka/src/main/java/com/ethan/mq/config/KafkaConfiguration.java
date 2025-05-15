/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@ConditionalOnProperty(prefix = "ethan.kafka", value = "enable", matchIfMissing = true)
@EnableAutoConfiguration(exclude= KafkaAutoConfiguration.class)
public class KafkaConfiguration {
    @Bean
    @Primary
    public CommonErrorHandler kafkaErrorHandler(KafkaTemplate<?, ?> template) {
        // <1> 创建 DeadLetterPublishingRecoverer 对象
        ConsumerRecordRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        // <2> 创建 FixedBackOff 对象
        BackOff backOff = new FixedBackOff(10 * 1000L, 3L);
        // <3> 创建 SeekToCurrentErrorHandler 对象
        return new DefaultErrorHandler(recoverer, backOff);
    }

}
