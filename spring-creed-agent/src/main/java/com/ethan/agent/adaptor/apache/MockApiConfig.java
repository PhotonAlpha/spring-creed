package com.ethan.agent.adaptor.apache;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 21/11/24
 */
@Data
public class MockApiConfig {
    private ServerDetails server = new ServerDetails();
    private List<String> apis = Collections.emptyList();
    private List<String> excludeApis = Collections.emptyList();

    @Data
    public static class ServerDetails {
        private Boolean enabled = false;
        private Boolean mockApiEnabled = false;
        private String url = "http://localhost:3000";
    }
}
