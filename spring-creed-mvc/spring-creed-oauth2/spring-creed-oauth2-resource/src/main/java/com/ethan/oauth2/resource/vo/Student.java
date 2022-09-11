package com.ethan.oauth2.resource.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 7/28/2022 5:17 PM
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Student {
    private String name;
    private String age;
}

