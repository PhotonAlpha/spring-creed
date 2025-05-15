package com.ethan.controller.activity.activity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityInfoVo {
    private String productName;
    private String discountPercentage = "10";
}
