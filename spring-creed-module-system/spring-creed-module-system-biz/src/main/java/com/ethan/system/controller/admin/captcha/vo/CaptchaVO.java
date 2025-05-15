/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */

package com.ethan.system.controller.admin.captcha.vo;

import lombok.Data;

import java.awt.*;
import java.util.List;

@Data
public class CaptchaVO {
    private String captchaId;
    private String projectCode;
    private String captchaType;
    private String captchaOriginalPath;
    private String captchaFontType;
    private Integer captchaFontSize;
    private String secretKey;
    private String originalImageBase64;
    private PointVO point;
    private String jigsawImageBase64;
    private List<String> wordList;
    private List<Point> pointList;
    private String pointJson;
    private String token;
    private Boolean result = false;
    private String captchaVerification;
    private String clientUid;
    private Long ts;
    private String browserInfo;
}
