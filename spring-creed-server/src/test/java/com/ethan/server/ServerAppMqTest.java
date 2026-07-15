package com.ethan.server;

import com.ethan.security.websecurity.entity.CreedAuthorities;
import com.ethan.security.websecurity.repository.CreedAuthorityRepository;
import com.ethan.system.mq.producer.dept.DeptStreamProducer;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import org.junit.jupiter.api.Disabled;

@SpringBootTest(classes = ServerApplication.class)
@ActiveProfiles(value = {"redis", "test", "shardingsphere"})
@Disabled("依赖本地 MQ，Spring 上下文无法启动，CI 暂禁用")
public class ServerAppMqTest {
    @Resource
    private DeptStreamProducer deptStreamProducer;

    @Test
    public void streamTest() {
        deptStreamProducer.sendDeptRefreshMessage();

    }


    @Resource
    private CreedAuthorityRepository authorityRepository;

    @Test
    void testQuery() {
        List<CreedAuthorities> all = authorityRepository.findAll();
        System.out.println(all);
    }
}
