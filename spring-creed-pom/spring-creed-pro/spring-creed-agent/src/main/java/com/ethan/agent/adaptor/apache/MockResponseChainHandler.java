package com.ethan.agent.adaptor.apache;

import com.ethan.agent.adaptor.MockApiConfigResolver;
import com.ethan.agent.factory.filter.RouteRegisterEndpointFilter;
import com.ethan.agent.util.CloseableHttpClientTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.ExecChain;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 为 Apache Http Client添加拦截器
 */
@Slf4j
public class MockResponseChainHandler implements ExecChainHandler, ApplicationContextAware {
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
        String uri = request.getPath();
        String convertedName = ConfigurationPropertyName.adapt(uri, '_').toString();
        MockApiConfig mockApiConfig = RouteRegisterEndpointFilter.getMockApiConfig();
        Boolean mockEnabled = MockApiConfigResolver.compute().apply(mockApiConfig, convertedName);
        log.debug("@.@[convertedUri:{} computed mockEnabled:{}]@.@", convertedName, mockEnabled);
        if (Boolean.FALSE.equals(mockEnabled)) {
            return chain.proceed(request, scope);
        }
        // now support application/json only
        String contentType = request.getEntity().getContentType();
        if (!StringUtils.equalsAnyIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE)) {
            return chain.proceed(request, scope);
        }
        UriComponents endpoint = UriComponentsBuilder.fromUriString(uri).build(true);
        UriComponents replacement = UriComponentsBuilder.fromUriString(mockApiConfig.getServer().getUrl()).build(true);
        UriComponents newEndpoint = UriComponentsBuilder.fromUriString(uri)
                .scheme(replacement.getScheme())
                .host(replacement.getHost())
                .port(replacement.getPort()).build(true);
        // check URI context path is matching in config
        var method = request.getMethod();
        return CloseableHttpClientTemplate.exchange(new BasicClassicHttpRequest(method, newEndpoint.toUri()));
    }

    private Predicate<Map.Entry<String, String>> contextPathPreffixPredicate(String convertedName) {
        return entry -> {
            var maskedContextPath = ConfigurationPropertyName.adapt(entry.getValue(), '_').toString();
            return StringUtils.containsIgnoreCase(maskedContextPath, convertedName);
        };
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
