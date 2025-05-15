/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.controller.admin.captcha.vo;

import lombok.Data;

@Data
public class ResponseModel {
    private String repCode;
    private String repMsg;
    private CaptchaVO repData;
}
