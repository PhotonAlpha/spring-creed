package com.ethan.example.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author EthanCao
 * @description spring-creed
 * @date 30/4/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertiesDto {
    private String application;
    private String profile;
    private String label;
    private String propertyKey;
    private String propertyValue;
}
