package com.ethan.controller.userlogin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo {
    private String name;
    private Boolean locked;
    private Boolean active;
}
