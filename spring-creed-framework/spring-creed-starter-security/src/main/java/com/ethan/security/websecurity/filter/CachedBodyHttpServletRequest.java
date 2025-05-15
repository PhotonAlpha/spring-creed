/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.security.websecurity.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.Part;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

/**
 * 拦截HttpServletRequest，并且可以重复读取
 * @author: EthanCao
 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
    private byte[] body;
    private BufferedReader reader;
    private ServletInputStream inputStream;
    private Collection<Part> parts;

    /**
     * 处理Multipart/form类型和别的post类型都不太一样。
     * x-www-form-urlencoded和普通post携带参数都是直接从request的inpustream里读取，
     * Multipart/form类型你需要获取Parts （request.getParts）
     *
     * @param request
     * @throws IOException
     * @throws ServletException
     */
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException, ServletException {
        super(request);
        if (StringUtils.startsWith(request.getContentType(), MediaType.MULTIPART_FORM_DATA_VALUE)) {
            parts = request.getParts();
        } else {
            loadBody(request);
        }
    }
    private void loadBody(HttpServletRequest request) throws IOException{
        body = StreamUtils.copyToByteArray(request.getInputStream());
        inputStream = new RequestCachingInputStream(body);
    }
    public byte[] getBody() {
        return body;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (inputStream != null) {
            return inputStream;
        }
        return super.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (reader == null) {
            reader = new BufferedReader(new InputStreamReader(inputStream, getCharacterEncoding()));
        }
        return reader;
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return this.parts;
    }

    private static class RequestCachingInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;
        public RequestCachingInputStream(byte[] bytes) {
            inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // Nothing
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }
}
