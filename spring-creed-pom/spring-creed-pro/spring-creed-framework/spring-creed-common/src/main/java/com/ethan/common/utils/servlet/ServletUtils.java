package com.ethan.common.utils.servlet;

import com.ethan.common.exception.ServerException;
import com.ethan.common.utils.json.JacksonUtils;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.Writer;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@UtilityClass
public class ServletUtils {
    /**
     * 返回 JSON 字符串
     *
     * @param response 响应
     * @param object 对象，会序列化成 JSON 字符串
     */
    public void writeJSON(HttpServletResponse response, Object object) {
        String content = JacksonUtils.toJsonString(object);
        write(response, content, MediaType.APPLICATION_JSON_VALUE);
    }

    public void write(HttpServletResponse response, String text, String contentType) {
        response.setContentType(contentType);
        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        try (Writer writer = response.getWriter()) {
            writer.write(text);
            writer.flush();
        } catch (IOException e) {
            throw new ServerException(e);
        }
    }

    /**
     * 返回附件
     *
     * @param response 响应
     * @param filename 文件名
     * @param content 附件内容
     * @throws IOException
     */
    public void writeAttachment(HttpServletResponse response, String filename, byte[] content) throws IOException {
        // 设置 header 和 contentType
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // 输出附件
        IOUtils.write(content, response.getOutputStream());
    }

    /**
     * @param request 请求
     * @return ua
     */
    public String getUserAgent(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua != null ? ua : "";
    }

    /**
     * 获得请求
     *
     * @return HttpServletRequest
     */
    public HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    public String getUserAgent() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getUserAgent(request);
    }

    public String getClientIP() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return getClientIP(request);
    }

    public boolean isJsonRequest(ServletRequest request) {
        return StringUtils.startsWithIgnoreCase(request.getContentType(), MediaType.APPLICATION_JSON_VALUE);
    }

    public String getClientIP(HttpServletRequest request, String... otherHeaderNames) {
        String[] headers = new String[]{"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        if (!ObjectUtils.isEmpty(otherHeaderNames)) {
            headers = Stream.concat(Stream.ofNullable(otherHeaderNames), Stream.ofNullable(headers))
                    .toArray(String[]::new);
        }

        return getClientIPByHeader(request, headers);
    }

    public String getClientIPByHeader(HttpServletRequest request, String... headerNames) {
        String[] var3 = headerNames;
        int var4 = headerNames.length;

        String ip;
        for(int var5 = 0; var5 < var4; ++var5) {
            String header = var3[var5];
            ip = request.getHeader(header);
            if (!isUnknown(ip)) {
                return getMultistageReverseProxyIp(ip);
            }
        }

        ip = request.getRemoteAddr();
        return getMultistageReverseProxyIp(ip);
    }


    public String getMultistageReverseProxyIp(String ip) {
        if (ip != null && ip.indexOf(",") > 0) {
            String[] ips = ip.trim().split(",");
            String[] var2 = ips;
            int var3 = ips.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String subIp = var2[var4];
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }

        return ip;
    }

    public boolean isUnknown(String checkString) {
        return !StringUtils.hasText(checkString) || "unknown".equalsIgnoreCase(checkString);
    }


}
