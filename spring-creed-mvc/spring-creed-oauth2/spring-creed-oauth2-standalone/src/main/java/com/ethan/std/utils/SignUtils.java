package com.ethan.std.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/18/2022 6:19 PM
 */
public class SignUtils {
    private static final Logger log = LoggerFactory.getLogger(SignUtils.class);
    public static final ObjectMapper MAPPER = new ObjectMapper();
    private SignUtils() {
    }

    public static String signature(String timestamp, String nonce, String requestPayload, HttpServletRequest request) {
        Assert.isTrue(StringUtils.isNotBlank(timestamp), "timestamp can not be empty");
        Assert.isTrue(StringUtils.isNotBlank(nonce), "nonce can not be empty");
        Assert.isTrue(StringUtils.isNotBlank(requestPayload), "requestPayload can not be empty");

        Map<String, Object> params = new HashMap<>();
        Enumeration<String> enumeration = request.getParameterNames();
        if (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getParameter(name);
            params.put(name, value);
        }
        String paramString = sortQueryParamString(params);

        String qs = String.format("timestamp=%s&nonce=%s&%s", timestamp, nonce, paramString);
        log.info("requestPayload:{} qs:{}", requestPayload, qs);
        String sign = EncryptUtils.sha256(requestPayload + qs).toUpperCase();
        log.info("sign:{}", sign);
        return sign;
    }

    private static String sortQueryParamString(Map<String, Object> params) {
        if (params == null) {
            return null;
        }
        List<String> listKeys = new ArrayList<>(params.keySet());
        List<String> paramStr = listKeys.stream().sorted(Comparator.naturalOrder())
                .map(name -> String.format("%s=%s", name, params.get(name)))
                .collect(Collectors.toList());
        return String.join("&", paramStr);
    }

}
