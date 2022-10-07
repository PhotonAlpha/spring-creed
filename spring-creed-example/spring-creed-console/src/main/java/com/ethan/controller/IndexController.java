/**
 * Copyright the original author or authors.
 *
 * @author Ethen Cao
 */
package com.ethan.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @program: spring-boot
 * @description: TODO
 * @author: 411084090@qq.com
 * @creat_date: 2018-09-24 17:04
 **/
@Controller
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    /**
     * @return index page
     */
    @RequestMapping({"/console/**", ""})
    public String index() {
        log.info("--->index");
        return "forward:/index.html";
    }
}
