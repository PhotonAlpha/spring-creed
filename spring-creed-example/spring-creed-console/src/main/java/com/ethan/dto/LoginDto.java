/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.dto;

import lombok.Data;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/20/2022 10:32 AM
 */
@Data
public class LoginDto {
    private String username;
    private String password;
}
