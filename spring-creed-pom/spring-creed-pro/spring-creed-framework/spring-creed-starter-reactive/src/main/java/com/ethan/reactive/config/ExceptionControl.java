/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.reactive.config;

import com.ethan.reactive.controller.vo.menu.MenuBaseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionControl {

    @ExceptionHandler(Exception.class)
    public MenuBaseVO handlerMaxUploadSizeExceededException(Exception ex) {
        log.error("[ex]", ex);
        MenuBaseVO baseVO = new MenuBaseVO();
        baseVO.setName("未知错误：" + ex.getMessage());
        return baseVO;
    }

}
