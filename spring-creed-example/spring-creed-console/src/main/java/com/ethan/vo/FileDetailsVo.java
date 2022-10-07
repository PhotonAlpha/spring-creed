/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.vo;

import lombok.Data;

/**
 * @description: vue-console
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/15/2022 2:44 PM
 */
@Data
public class FileDetailsVo {
    private String name;
    private String url;

    public FileDetailsVo() {
    }

    public FileDetailsVo(String name) {
        this.name = name;
    }
}
