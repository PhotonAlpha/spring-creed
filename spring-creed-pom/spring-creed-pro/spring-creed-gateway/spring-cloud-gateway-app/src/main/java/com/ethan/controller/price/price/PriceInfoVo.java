package com.ethan.controller.price.price;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceInfoVo {
    private String productName;
    private BigDecimal price;
}
