package com.ethan.controller.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoVo {
    private String productName;
    private String brand;
    private String description;
    private Long stock;
    private BigDecimal price;
}
