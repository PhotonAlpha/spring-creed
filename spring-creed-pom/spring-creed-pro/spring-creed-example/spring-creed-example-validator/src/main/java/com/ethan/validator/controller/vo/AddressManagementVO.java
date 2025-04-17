package com.ethan.validator.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 20/2/25
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AddressManagementVO {
    private List<String> tags;
    private List<String> customTags;
    private List<AddressVO> address;
}
