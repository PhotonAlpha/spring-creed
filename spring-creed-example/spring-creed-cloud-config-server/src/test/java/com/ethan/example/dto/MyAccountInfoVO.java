package com.ethan.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyAccountInfoVO {

    private String id;

    private String code;

    private String name;


    private String password;


    private String email;


    private String sex;

    private String phone;

}
