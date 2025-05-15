/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsumerSample {
    @BeforeAll
    public static void init() {
        Logger.getLogger(ConsumerSample.class.getName()).setLevel(Level.FINE);
    }

    @Test
    void consumer() {
        String topic = "test-topic";// topic name
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");//用于建立与 kafka 集群连接的 host/port 组。
        props.put("group.id", "testGroup1");// Consumer Group Name
        props.put("enable.auto.commit", "true");// Consumer 的 offset 是否自动提交
        props.put("auto.commit.interval.ms", "1000");// 自动提交 offset 到 zookeeper 的时间间隔，时间是毫秒
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        Consumer<String, String> consumer = new KafkaConsumer(props);
        consumer.subscribe(Arrays.asList(topic));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records)
                System.out.printf("partition = %d, offset = %d, key = %s, value = %s%n", record.partition(), record.offset(), record.key(), record.value());
        }
    }
}
