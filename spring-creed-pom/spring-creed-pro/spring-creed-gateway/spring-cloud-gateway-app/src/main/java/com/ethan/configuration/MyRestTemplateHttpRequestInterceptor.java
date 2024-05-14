package com.ethan.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class MyRestTemplateHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // log the http request
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("------- API Request -------");
        log.info("URI         : {}", request.getURI());
        log.info("Request Headers     : {}", request.getHeaders());
        log.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        log.info("------- API Response -------");
        log.info("Status code  : {}", response.getStatusCode());
        // log.info("Status text  : {}", response.getStatusText());
        log.info("Response Headers      : {}", response.getHeaders());
        log.info("Response body: {}", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
    }

}
