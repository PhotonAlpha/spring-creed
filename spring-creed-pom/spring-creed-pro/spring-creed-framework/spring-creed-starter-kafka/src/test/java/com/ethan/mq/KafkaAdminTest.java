/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.mq;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

public class KafkaAdminTest {
    private static final String NEW_TOPIC = "topic-test2";
    private static final String brokerUrl = "localhost:9092";
    private static AdminClient adminClient;

    @BeforeAll
    public static void init() {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, brokerUrl);
        adminClient = AdminClient.create(properties);
    }

    @AfterAll
    public static void close() {
        adminClient.close();
    }

    @Test
    public void createTopics() {
        NewTopic newTopic = new NewTopic(NEW_TOPIC,4, (short) 1);
        Collection<NewTopic> newTopicList = new ArrayList<>();
        newTopicList.add(newTopic);
        adminClient.createTopics(newTopicList);
    }
}
