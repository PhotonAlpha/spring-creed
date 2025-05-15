package com.ethan.framework.openapi.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * Swagger 配置属性
 *
 * 
 */
@ConfigurationProperties("ethan.swagger")
@Data
public class OpenApiProperties {

    /**
     * 标题
     */
    @NotEmpty(message = "标题不能为空")
    private String title= "spring creed mall";
    /**
     * 描述
     */
    @NotEmpty(message = "描述不能为空")
    private String description = "spring creed mall practice";
    /**
     * 作者
     */
    @NotEmpty(message = "作者不能为空")
    private String author = "ethan.caoq@foxmail.com";
    /**
     * 版本
     */
    @NotEmpty(message = "版本不能为空")
    private String version = "1.0";
    /**
     * 扫描的包
     */
    @NotEmpty(message = "扫描的 package 不能为空")
    private String[] basePackage = {"com.ethan.system.controller"};
}
