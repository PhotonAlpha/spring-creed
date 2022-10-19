/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.controller;

import com.ethan.dto.CommandReqDto;
import com.ethan.dto.DownloadReqDto;
import com.ethan.service.ExecutionServiceImpl;
import com.ethan.vo.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.weaver.ast.Var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.sleuth.http.HttpServerRequest;
import org.springframework.cloud.sleuth.http.HttpServerResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @description: vue-console
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/14/2022 4:48 PM
 */
@RestController
@RequestMapping("/api/v1")
public class ExecutionController {
    private static final Logger log = LoggerFactory.getLogger(ExecutionController.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    @Resource
    private ExecutionServiceImpl executionService;

    @PostMapping("/exec")
    public R executeCommand(@RequestBody CommandReqDto dto) {
        return executionService.executeCommand(dto);
    }


    @PostMapping("/download-repo")
    public void downloadRepo(@RequestBody DownloadReqDto dto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Path path = Path.of(dto.getPath());
        log.info("starting downloadRepo:{}", path);
        FileSystemResource fileResource = new FileSystemResource(path);
        if (!fileResource.exists()) {
            response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Expires", "0");

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
            response.getWriter().write(MAPPER.writeValueAsString(R.error(400, "File not exist!")));
        } else {
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + path.getFileName());
            response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.addHeader("Pragma", "no-cache");
            response.addHeader("Expires", "0");

            try (InputStream inputStream = fileResource.getInputStream();
                 BufferedInputStream in = new BufferedInputStream(inputStream);
                 BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {
                FileCopyUtils.copy(in, out);
            }

        }
    }
    @PostMapping("/file-remove")
    public R removeFile(@RequestBody DownloadReqDto dto) throws IOException {
        Path path = Path.of(dto.getPath());
        FileSystemResource fileResource = new FileSystemResource(path);
        if (!fileResource.exists()) {
            return R.error(400, "file not exist");
        } else {
            Files.delete(path);
            return R.success( "remove successful");
        }
    }

}
