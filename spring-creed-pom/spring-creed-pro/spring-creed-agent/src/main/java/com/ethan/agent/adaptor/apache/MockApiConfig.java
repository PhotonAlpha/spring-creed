package com.ethan.agent.adaptor.apache;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 21/11/24
 */
@Data
public class MockApiConfig {
    private Map<String, Map<String, List<MockMatrixConfig>>> apis = new HashMap<>();
    private Map<String, String> contextPath = new HashMap<>();
}
