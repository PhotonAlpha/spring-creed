package com.ethan.server;

import com.ethan.system.mq.producer.dept.DeptStreamProducer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ServerApplication.class)
@ActiveProfiles(value = {"redis", "test"})
public class ServerAppMqTest {
    @Resource
    private DeptStreamProducer deptStreamProducer;

    @Test
    public void streamTest() {
        deptStreamProducer.sendDeptRefreshMessage();

    }
}
