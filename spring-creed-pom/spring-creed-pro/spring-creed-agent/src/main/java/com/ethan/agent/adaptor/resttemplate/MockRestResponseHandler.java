package com.ethan.agent.adaptor.resttemplate;

import com.ethan.agent.adaptor.MockApiConfigResolver;
import com.ethan.agent.adaptor.apache.MockApiConfig;
import com.ethan.agent.factory.filter.RouteRegisterEndpointFilter;
import com.ethan.agent.util.CloseableHttpClientTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.message.BasicClassicHttpRequest;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 26/11/24
 */
@Slf4j
public class MockRestResponseHandler implements ClientHttpRequestInterceptor, ApplicationContextAware, Ordered {
    private ApplicationContext applicationContext;

    public MockRestResponseHandler() {
    }

    public MockRestResponseHandler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String contentType = Optional.ofNullable(request.getHeaders().getContentType())
                .map(MediaType::toString).orElse(MediaType.APPLICATION_JSON_VALUE);
        if (!StringUtils.equalsAnyIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE)) {
            return execution.execute(request, body);
        }
        String convertedName = ConfigurationPropertyName.adapt(request.getURI().getPath(), '_').toString();
        MockApiConfig mockApiConfig = RouteRegisterEndpointFilter.getMockApiConfig();
        Boolean mockEnabled = MockApiConfigResolver.compute().apply(mockApiConfig, convertedName);
        log.debug("@.@[convertedUri:{} computed mockEnabled:{}]@.@", convertedName, mockEnabled);
        if (Boolean.FALSE.equals(mockEnabled)) {
            return execution.execute(request, body);
        }

        var requestMethod = request.getMethod().name();
        UriComponents endpoint = UriComponentsBuilder.fromUri(request.getURI()).build(true);
        UriComponents replacement = UriComponentsBuilder.fromUriString(mockApiConfig.getServer().getUrl()).build(true);
        UriComponents newEndpoint = UriComponentsBuilder.fromUri(request.getURI())
                .scheme(replacement.getScheme())
                .host(replacement.getHost())
                .port(replacement.getPort()).build(true);
        log.info("@.@[<<execute>>change url from:{} to:{}]@.@", endpoint, newEndpoint);

        ClassicHttpResponse httpResponse = CloseableHttpClientTemplate.exchange(new BasicClassicHttpRequest(requestMethod, newEndpoint.toUri()));
        return new MockClientHttpResponse(httpResponse.getEntity().getContent(), new HttpHeaders(), HttpStatus.resolve(httpResponse.getCode()));
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
        private final HttpStatus statusCode;
        private final HttpHeaders headers;
        private final InputStream body;

        public MockClientHttpResponse(InputStream body, HttpHeaders headers, HttpStatus statusCode) {
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = body;
        }

        public MockClientHttpResponse(byte[] body, HttpHeaders headers, HttpStatus statusCode) {
            this.statusCode = statusCode;
            this.headers = headers;
            this.body = new ByteArrayInputStream(body);
        }

        @Override
        public HttpStatus getStatusCode() throws IOException {
            return statusCode;
        }

        @Override
        public String getStatusText() throws IOException {
            if (Objects.nonNull(this.statusCode)) {
                return this.statusCode.getReasonPhrase();
            }
            return "";
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
