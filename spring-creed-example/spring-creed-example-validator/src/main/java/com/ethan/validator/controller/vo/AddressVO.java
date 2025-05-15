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
@Data
public class AddressVO {
    private String consignee;
    private String contactCountryCode;
    private String contactNo;
    private String location;
    private String addressDetail;
    @JsonProperty("default")
    private boolean defaultX;
    private String tag;
}
