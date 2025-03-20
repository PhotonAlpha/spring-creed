package com.ethan.identity.server.filter;

import com.ethan.identity.core.common.Result;
import com.ethan.identity.core.common.Status;
import com.ethan.identity.core.segment.SegmentIDGenImpl;
import com.ethan.identity.core.segment.dal.entity.SegmentBuffer;
import com.ethan.identity.core.segment.dal.entity.SystemLeafAlloc;
import com.ethan.identity.server.exception.LeafServerException;
import com.ethan.identity.server.exception.NoKeyException;
import com.ethan.identity.server.model.SegmentBufferView;
import com.ethan.identity.server.service.SegmentService;
import com.ethan.identity.server.service.SnowflakeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 30/12/24
 */
@Slf4j
public class IdGenerateConsoleFilter extends OncePerRequestFilter {
    private final GenericHttpMessageConverter<Object> jsonMessageConverter = new MappingJackson2HttpMessageConverter();
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String DEFAULT_SEGMENT_ENDPOINT_URI = "/leaf/segment/*";
    private static final String DEFAULT_SNOWFLAKE_ENDPOINT_URI = "/leaf/snowflake/*";
    private static final String DEFAULT_CACHE_ENDPOINT_URI = "/leaf/cache";
    private static final String DEFAULT_DB_ENDPOINT_URI = "/leaf/db";
    private static final String DEFAULT_DECODE_SNOWFLAKE_ID_ENDPOINT_URI = "/leaf/decodeSnowflakeId";
    private static final String DEFAULT_ADD_BIZ_TAG_ENDPOINT_URI = "/leaf/add-biz-tag";
    private static final String DEFAULT_REMOVE_BIZ_TAG_ENDPOINT_URI = "/leaf/remove-biz-tag";
    private final RequestMatcher authorizationEndpointMatcher;

    private final SegmentService segmentService;
    private final SnowflakeService snowflakeService;
    private ViewResolver viewResolver;

    public IdGenerateConsoleFilter(SegmentService segmentService, SnowflakeService snowflakeService, ViewResolver viewResolver) {
        this.authorizationEndpointMatcher = new OrRequestMatcher(
                createDefaultRequestMatcher(DEFAULT_SEGMENT_ENDPOINT_URI),
                createDefaultRequestMatcher(DEFAULT_SNOWFLAKE_ENDPOINT_URI),

                createDefaultRequestMatcher(DEFAULT_CACHE_ENDPOINT_URI),
                createDefaultRequestMatcher(DEFAULT_DB_ENDPOINT_URI),
                createDefaultRequestMatcher(DEFAULT_DECODE_SNOWFLAKE_ID_ENDPOINT_URI),
                createDefaultRequestMatcher(DEFAULT_ADD_BIZ_TAG_ENDPOINT_URI),
                createDefaultRequestMatcher(DEFAULT_REMOVE_BIZ_TAG_ENDPOINT_URI)
        );
        this.segmentService = segmentService;
        this.snowflakeService = snowflakeService;
        this.viewResolver = viewResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!this.authorizationEndpointMatcher.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(request.getRequestURI()).build();
        var key = uriComponents.getPathSegments().getLast();
        try {
            if (createDefaultRequestMatcher(DEFAULT_SEGMENT_ENDPOINT_URI).matches(request)) {
                sendSuccessfulResponse(response, segmentService.getId(key));
            } else if (createDefaultRequestMatcher(DEFAULT_SNOWFLAKE_ENDPOINT_URI).matches(request)) {
                sendSuccessfulResponse(response, snowflakeService.getId(key));
            } else if (createDefaultRequestMatcher(DEFAULT_DECODE_SNOWFLAKE_ID_ENDPOINT_URI).matches(request)) {
                sendSuccessfulResponse(response, decodeSnowflakeId(request, response));
            } else {
                sendMVCResponse(request, response);
            }
        } catch (Exception e) {
            sendFailedResponse(response, ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public Map<String, String> decodeSnowflakeId(HttpServletRequest request, HttpServletResponse response) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(request.getRequestURI()).build();
        var snowflakeIdStr = uriComponents.getQueryParams().getFirst("snowflakeId");
        Map<String, String> map = new HashMap<>();
        try {
            long snowflakeId = Long.parseLong(snowflakeIdStr);

            long originTimestamp = (snowflakeId >> 22) + 1288834974657L;
            Date date = new Date(originTimestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            map.put("timestamp", String.valueOf(originTimestamp) + "(" + sdf.format(date) + ")");

            long workerId = (snowflakeId >> 12) ^ (snowflakeId >> 22 << 10);
            map.put("workerId", String.valueOf(workerId));

            long sequence = snowflakeId ^ (snowflakeId >> 12 << 12);
            map.put("sequenceId", String.valueOf(sequence));
        } catch (NumberFormatException e) {
            map.put("errorMsg", "snowflake Id反解析发生异常!");
        }
        return map;
    }

    public void addBizTag(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(request.getRequestURI())
                .query(request.getQueryString())
                .build();
        var queryParams = uriComponents.getQueryParams();
        String bizTag = queryParams.getFirst("bizTag");

        int maxId = NumberUtils.toInt(queryParams.getFirst("maxId"), -1);
        String description = queryParams.getFirst("description");
        int step = NumberUtils.toInt(queryParams.getFirst("step"), -1);

        Preconditions
                .checkArgument(StringUtils.hasText(bizTag) && StringUtils.hasText(description), "bizTag or description must not" +
                        " be null");
        Preconditions
                .checkArgument(maxId > 0 && step > 0, "maxId or step must be positive");

        SegmentIDGenImpl segmentIDGen = segmentService.getIdGen();
        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enabled=true first");
        }
        SystemLeafAlloc temp = new SystemLeafAlloc();
        temp.setBizTag(bizTag);
        temp.setStep(step);
        temp.setMaxId(maxId);
        temp.setDescription(description);
        temp.setUpdateTime(Instant.now());
        SystemLeafAlloc leafAlloc = segmentIDGen.addLeafAlloc(temp);
        log.info("add leafAlloc info {}", leafAlloc);
        renderDBPage(request, response);

    }

    public void removeBizTag(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(request.getRequestURI())
                .query(request.getQueryString())
                .build();
        var queryParams = uriComponents.getQueryParams();
        String bizTag = queryParams.getFirst("bizTag");
        Preconditions
                .checkArgument(StringUtils.hasText(bizTag), "bizTag must not be null");

        SegmentIDGenImpl segmentIDGen = segmentService.getIdGen();
        if (segmentIDGen == null) {
            throw new IllegalArgumentException("You should config leaf.segment.enabled=true first");
        }
        segmentIDGen.removeLeafAlloc(bizTag);
        log.info("remove bizTag {}", bizTag);
        renderDBPage(request, response);
    }

    private static RequestMatcher createDefaultRequestMatcher(String endpointUri) {
        return new AntPathRequestMatcher(endpointUri, HttpMethod.GET.name());
    }
    private String get(@PathVariable("key") String key, Result id) {
        Result result;
        if (key == null || key.isEmpty()) {
            throw new NoKeyException();
        }
        result = id;
        if (result.getStatus().equals(Status.EXCEPTION)) {
            throw new LeafServerException(result.toString());
        }
        return String.valueOf(result.getId());
    }

    private void sendSuccessfulResponse(HttpServletResponse response, Object responseBody) throws IOException {
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.jsonMessageConverter.write(responseBody, MediaType.APPLICATION_JSON, httpResponse);
    }

    private void sendFailedResponse(HttpServletResponse response, String errorMessage) throws IOException {
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        httpResponse.setStatusCode(HttpStatus.OK);
        this.jsonMessageConverter.write(Map.of("errorMessage", errorMessage), MediaType.APPLICATION_JSON, httpResponse);
    }
    private void renderFailedPage(HttpServletRequest request, HttpServletResponse response, String errorMessage, String viewName) throws ServletException {
        try {
            viewResolver.resolveViewName("template-error", Locale.getDefault()).render(Map.of("errorMessage", errorMessage), request, response);
        } catch (Exception ex) {
            log.error("Unable to resolve view {}", viewName, ex);
            throw new ServletException("Unable to resolve view", ex);
        }
    }

    private void sendMVCResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
        try {
            if (createDefaultRequestMatcher(DEFAULT_CACHE_ENDPOINT_URI).matches(request)) {
                renderCachePage(request, response);
            } else if (createDefaultRequestMatcher(DEFAULT_DB_ENDPOINT_URI).matches(request)) {
                renderDBPage(request, response);
            } else if (createDefaultRequestMatcher(DEFAULT_ADD_BIZ_TAG_ENDPOINT_URI).matches(request)) {
                addBizTag(request, response);
            } else if (createDefaultRequestMatcher(DEFAULT_REMOVE_BIZ_TAG_ENDPOINT_URI).matches(request)) {
                removeBizTag(request, response);
            }
        } catch (Exception e) {
            log.error("sendMVCResponse exception", e);
            renderFailedPage(request, response, e.getMessage(), "");
        }
    }

    public void renderCachePage(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String viewName = "segment";
        try {
            Map<String, SegmentBufferView> segmentBuffer = new HashMap<>();
            SegmentIDGenImpl segmentIDGen = segmentService.getIdGen();
            if (segmentIDGen == null) {
                throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
            }
            Map<String, SegmentBuffer> cache = segmentIDGen.getCache();
            for (Map.Entry<String, SegmentBuffer> entry : cache.entrySet()) {
                SegmentBufferView sv = new SegmentBufferView();
                SegmentBuffer buffer = entry.getValue();
                sv.setInitOk(buffer.isInitOk());
                sv.setKey(buffer.getKey());
                sv.setPos(buffer.getCurrentPos());
                sv.setNextReady(buffer.isNextReady());
                sv.setMax0(buffer.getSegments()[0].getMax());
                sv.setValue0(buffer.getSegments()[0].getValue().get());
                sv.setStep0(buffer.getSegments()[0].getStep());

                sv.setMax1(buffer.getSegments()[1].getMax());
                sv.setValue1(buffer.getSegments()[1].getValue().get());
                sv.setStep1(buffer.getSegments()[1].getStep());

                segmentBuffer.put(entry.getKey(), sv);

            }
            log.info("Cache info {}", segmentBuffer);


            // Use the viewResolver to find the view for the default locale
            View resolvedView = viewResolver.resolveViewName(viewName, Locale.getDefault());

            // Instantiate the model map
            Map<String, Object> model = new HashMap<>();
            // Here we can add a value in the model that can be used in the Thymeleaf template
            model.put("data", segmentBuffer);

            // Renders the view on the request/responses using the model map.
            resolvedView.render(model, request, response);

            // The request has been processed, so return
            return;
        } catch (Exception e) {
            renderFailedPage(request, response, e.getMessage(), viewName);
        }
    }

    public void renderDBPage(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String viewName = "db";
        try {
            SegmentIDGenImpl segmentIDGen = segmentService.getIdGen();
            if (segmentIDGen == null) {
                throw new IllegalArgumentException("You should config leaf.segment.enable=true first");
            }
            List<SystemLeafAlloc> items = segmentIDGen.getAllLeafAllocs();
            log.info("DB info {}", items);

            // Use the viewResolver to find the view for the default locale
            View resolvedView = viewResolver.resolveViewName(viewName, Locale.ENGLISH);

            // Instantiate the model map
            Map<String, Object> model = new HashMap<>();
            // Here we can add a value in the model that can be used in the Thymeleaf template
            model.put("items", items);

            // Renders the view on the request/responses using the model map.
            resolvedView.render(model, request, response);

            // The request has been processed, so return
            return;
        } catch (Exception e) {
            renderFailedPage(request, response, e.getMessage(), viewName);
        }
    }
}
