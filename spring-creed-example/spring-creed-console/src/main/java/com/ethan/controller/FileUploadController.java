/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.controller;

import com.ethan.dto.CommandReqDto;
import com.ethan.service.ExecutionServiceImpl;
import com.ethan.service.FileUploadServiceImpl;
import com.ethan.vo.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @description: vue-console
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/14/2022 4:48 PM
 */
@RestController
@RequestMapping("/api/v1")
public class FileUploadController {
    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    @Resource
    private FileUploadServiceImpl uploadService;


    @PostMapping("/upload")
    public R<String> fileUpload(@RequestParam("files") MultipartFile[] files, @RequestParam("pwd") String pwd) {
        return uploadService.handlerFileUpload(files, pwd);
    }
    @GetMapping("/list-files")
    public R<String> listFiles() {
        return uploadService.listFiles();
    }
}
