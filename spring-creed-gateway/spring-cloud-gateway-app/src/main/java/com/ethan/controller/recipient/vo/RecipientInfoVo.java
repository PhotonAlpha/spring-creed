package com.ethan.controller.recipient.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientInfoVo {
    private String name;
    private String contactInfo;
    private String country;
    private String address;
}
