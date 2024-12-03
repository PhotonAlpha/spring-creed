package com.ethan.agent.adaptor.apache;

import com.ethan.agent.adaptor.MockApiConfigResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

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
 * 为 Apache Http Client添加拦截器
 */
@Slf4j
public class MockResponseChainHandler implements ExecChainHandler, ApplicationContextAware, MockApiConfigResolver {
    private ApplicationContext applicationContext;
    private MockApiConfig cachedMockApiConfig;

    /**
     * SystemEnvironmentPropertyMapper#convertName
     *
     * @param request the actual request.
     * @param scope   the execution scope .
     * @param chain   the next element in the request execution chain.
     * @return
     * @throws IOException
     * @throws HttpException
     */
    @Override
    public ClassicHttpResponse execute(ClassicHttpRequest request, ExecChain.Scope scope, ExecChain chain) throws IOException, HttpException {
        if (Objects.isNull(cachedMockApiConfig)) {
            cachedMockApiConfig = resolveProperties(applicationContext);
        }

        // now support application/json only
        String contentType = request.getEntity().getContentType();
        if (!StringUtils.equalsAnyIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE)) {
            return chain.proceed(request, scope);
        }
        var contextPathMapping = Optional.ofNullable(cachedMockApiConfig.getContextPath()).orElse(Map.of("default", "cew"));
        String convertedName = ConfigurationPropertyName.adapt(request.getPath(), '_').toString();
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
        if (StringUtils.equalsAnyIgnoreCase(request.getMethod(), HttpMethod.GET.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name())) {
            // TODO consider  check condition base on request parameters if is GET
            matrixConfig = mockApiConfigs.stream().findAny().orElse(null);
        } else {
            var requestPayload = IOUtils.toString(request.getEntity().getContent(), StandardCharsets.UTF_8);
            log.info("API Request Payload:{}", requestPayload);
            matrixConfig = conditionMatchCheck(mockApiConfigs, requestPayload);
        }
        if (Objects.isNull(matrixConfig)) {
            log.warn("no matrixConfig detected! Request Method:{} request converted property key:{}", request.getMethod(), convertedName);
            return chain.proceed(request, scope);
        }
        log.info("mock response from path:{}", matrixConfig.getPath());
        ClassicHttpResponse response = new BasicClassicHttpResponse(HttpStatus.SC_OK);
        try (InputStream in = new FileSystemResource(matrixConfig.getPath()).getInputStream()) {
            response.setEntity(new StringEntity(IOUtils.toString(in, StandardCharsets.UTF_8), ContentType.APPLICATION_JSON));
        }
        return response;
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
                List answer = Collections.emptyList();//JsonPath.parse(requestPayload).read(apiConfig.getCondition(), List.class);
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
}
