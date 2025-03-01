package com.ethan.agent.adaptor.resttemplate;

import com.ethan.agent.adaptor.MockApiConfigResolver;
import com.ethan.agent.adaptor.apache.MockApiConfig;
import com.ethan.agent.adaptor.apache.MockMatrixConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 26/11/24
 */
@Slf4j
public class MockRestResponseHandler implements ClientHttpRequestInterceptor, ApplicationContextAware, MockApiConfigResolver, Ordered {
    private ApplicationContext applicationContext;
    private MockApiConfig cachedMockApiConfig;

    public MockRestResponseHandler() {
    }

    public MockRestResponseHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        if (Objects.isNull(cachedMockApiConfig)) {
            cachedMockApiConfig = resolveProperties(applicationContext);
        }

        // now support application/json only

        String contentType = Optional.ofNullable(request.getHeaders().getContentType())
                .map(MediaType::toString).orElse(MediaType.APPLICATION_JSON_VALUE);
        if (!StringUtils.equalsAnyIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE)) {
            return execution.execute(request, body);
        }
        var contextPathMapping = Optional.ofNullable(cachedMockApiConfig.getContextPath()).orElse(Map.of("default", "cew"));
        String convertedName = ConfigurationPropertyName.adapt(request.getURI().getPath(), '_').toString();
        // check URI context path is matching in config
        var apiKey = contextPathMapping
                .entrySet().stream()
                .filter(contextPathPreffixPredicate(convertedName))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(contextPathMapping.get("default"));

        var cewApiMappings = cachedMockApiConfig.getApis().getOrDefault(apiKey, Collections.emptyMap());
        List<MockMatrixConfig> mockApiConfigs = cewApiMappings.entrySet().stream().filter(entry -> StringUtils.equalsIgnoreCase(entry.getKey(), convertedName))
                .map(Map.Entry::getValue).flatMap(Collection::stream).toList();
        MockMatrixConfig matrixConfig;

        if (StringUtils.equalsAnyIgnoreCase(request.getMethod().name(), HttpMethod.GET.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name())) {
            // TODO consider  check condition base on request parameters if is GET
            matrixConfig = mockApiConfigs.stream().findAny().orElse(null);
        } else {
            var requestPayload = IOUtils.toString(body, StandardCharsets.UTF_8.displayName());
            log.info("API Request Payload:{}", requestPayload);
            matrixConfig = conditionMatchCheck(mockApiConfigs, requestPayload);
        }
        if (Objects.isNull(matrixConfig)) {
            log.warn("no matrixConfig detected! Request Method:{} request converted property key:{}", request.getMethod(), convertedName);
            return execution.execute(request, body);
        }
        log.info("mock response from path:{}", matrixConfig.getPath());
        try (InputStream in = new FileSystemResource(matrixConfig.getPath()).getInputStream()) {
            var httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return new MockClientHttpResponse(in.readAllBytes(), httpHeaders, HttpStatus.OK);
        }
    }

    private Predicate<Map.Entry<String, String>> contextPathPreffixPredicate(String convertedName) {
        return entry -> {
            var maskedContextPath = ConfigurationPropertyName.adapt(entry.getValue(), '_').toString();
            return StringUtils.containsIgnoreCase(maskedContextPath, convertedName);
        };
    }

    public MockMatrixConfig conditionMatchCheck(List<MockMatrixConfig> mockApiConfigs, String requestPayload) {
        List<MockMatrixConfig> mockApiConfigsWithCondition = mockApiConfigs.stream().filter(conf -> StringUtils.isNotBlank(conf.getCondition())).toList();
        for (MockMatrixConfig apiConfig : mockApiConfigsWithCondition) {
            try {
                List answer = Collections.emptyList();// JsonPath.parse(requestPayload).read(apiConfig.getCondition(), List.class);
                if (Objects.nonNull(answer) && !answer.isEmpty()) {
                    return apiConfig;
                }
            } catch (Exception e) {
                //do nothing, continue check
            }
        }
        List<MockMatrixConfig> mockApiConfigsBackoff = mockApiConfigs.stream().filter(conf -> StringUtils.isBlank(conf.getCondition())).toList();
        return mockApiConfigsBackoff.stream().findFirst().orElse(null);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    static class MockClientHttpResponse implements ClientHttpResponse {
        private final HttpStatusCode statusCode;
        private final HttpHeaders headers;
        private final InputStream body;

        public MockClientHttpResponse(InputStream body, HttpHeaders headers, HttpStatusCode statusCode) {
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = body;
        }

        public MockClientHttpResponse(byte[] body, HttpHeaders headers, HttpStatusCode statusCode) {
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = new ByteArrayInputStream(body);
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return statusCode;
        }

        @Override
        public String getStatusText() throws IOException {
            return (this.statusCode instanceof HttpStatus status ? status.getReasonPhrase() : "");
        }

        @Override
        public void close() {
            try {
                getBody().close();
            }
            catch (IOException ex) {
                // ignore
            }
        }

        @Override
        public InputStream getBody() throws IOException {
            return body;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }
    }
}
