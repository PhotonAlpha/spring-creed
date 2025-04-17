package com.ethan.validator.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 20/2/25
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealNameAuthenticationVO {
    @JsonProperty("name")
    private String nameX;
    @JsonProperty("identityNo")
    private String identityNo;
}
