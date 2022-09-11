package com.ethan.std.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/16/2022 11:31 AM
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Person {
    private String name;
    private String title;
}
