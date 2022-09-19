/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.controller;

import com.ethan.dto.CommandReqDto;
import com.ethan.service.ExecutionServiceImpl;
import com.ethan.vo.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @description: vue-console
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/14/2022 4:48 PM
 */
@RestController
@RequestMapping("/api/v1")
public class ExecutionController {
    @Resource
    private ExecutionServiceImpl executionService;

    @PostMapping("/exec")
    public R executeCommand(@RequestBody CommandReqDto dto) {
        return executionService.executeCommand(dto);
    }

}
