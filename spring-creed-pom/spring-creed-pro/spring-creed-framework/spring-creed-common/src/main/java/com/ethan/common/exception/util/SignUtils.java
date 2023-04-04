/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.common.exception.util;

import com.ethan.common.constant.CommonConstants;
import com.ethan.common.exception.enums.GlobalErrorCodeConstants;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.StringSubstitutor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

/**
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 3/22/2023 11:22 AM
 */
@UtilityClass
public class SignUtils {
    private static final String SIGN_TEMPLATE = "token=${token}&nonce=${nonce}&timestamp=${timestamp}&body=${body}";
    public static String generateSignature(String token, String nonce, String timestamp, String body) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put(CommonConstants.SIGN_TOKEN, token);
        valueMap.put(CommonConstants.SIGN_NONCE, nonce);
        valueMap.put(CommonConstants.SIGN_TIME, timestamp);
        valueMap.put("body", body);
        String plainText = StringSubstitutor.replace(SIGN_TEMPLATE, valueMap);
        return sha256Encode(plainText);
    }

    private static String sha256Encode(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (Exception e) {
            throw ServiceExceptionUtil.exception(GlobalErrorCodeConstants.AUTH_SIGNATURE_ERROR, ExceptionUtils.getRootCause(e));
        }
    }
}
