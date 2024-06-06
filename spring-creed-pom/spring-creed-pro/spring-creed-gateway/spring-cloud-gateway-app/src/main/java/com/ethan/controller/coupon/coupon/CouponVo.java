package com.ethan.controller.coupon.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponVo {
    private String userName;
    private BigDecimal discount;
}
