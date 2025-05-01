package com.ethan.validator.controller.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

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
public class MyAccountDetailsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4722565630055279004L;
    private String id;


    // @NotEmpty(message = "Code不能为空")
    private String code;

    @NotBlank(message = "名字为必填项")
    private String name;


    @Length(min = 8, max = 12, message = "password长度必须位于8到12之间")
    private String password;


    @Email(message = "请填写正确的邮箱地址")
    private String email;


    private String sex;

    private String phone;

    private ProfileVO profile;
}
