/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.vo;

import lombok.Data;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/20/2022 10:36 AM
 */
@Data
public class TokenVo {
    private String token;

    public TokenVo() {
    }

    public TokenVo(String token) {
        this.token = token;
    }
}
