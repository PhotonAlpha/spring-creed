package com.ethan.example.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 18/2/25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArtisanDetailsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4722565630055279004L;
    private String id;


    // @NotEmpty(message = "Code不能为空")
    private String code;

    private String name;


    private String password;


    private String email;


    private String sex;

    private String phone;

}
