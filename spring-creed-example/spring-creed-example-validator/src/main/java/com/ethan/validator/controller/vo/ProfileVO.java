package com.ethan.validator.controller.vo;

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
@Data
@Builder
public class ProfileVO {
    private String avatar;
    private String accountName;
    private String nickName;
    private AccountInfoVO accountInfo;
    private AddressManagementVO addressManagement;
}
