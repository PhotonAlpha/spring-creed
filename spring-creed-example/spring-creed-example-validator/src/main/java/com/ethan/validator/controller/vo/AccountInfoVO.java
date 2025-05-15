package com.ethan.validator.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 20/2/25
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class AccountInfoVO {
    private RealNameAuthenticationVO realNameAuthentication;
}
