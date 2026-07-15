package com.creed;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Disabled;

/**
 * @className: MyUnitTest
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Slf4j
@SpringBootTest
@ActiveProfiles("dev")
@Disabled("Spring 上下文无法启动，CI 暂禁用")
public class MyIntegrationTest {
    @Test
    void testInputOutput() {


    }
}
