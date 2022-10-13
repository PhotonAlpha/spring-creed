/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/20/2022 11:08 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoVo {
    private List<String> roles;
    private String introduction;
    private String avatar;
    private String name;
}
