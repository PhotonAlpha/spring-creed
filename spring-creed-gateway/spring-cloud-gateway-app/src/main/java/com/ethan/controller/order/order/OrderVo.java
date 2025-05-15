package com.ethan.controller.order.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo {
    private String orderNo;
    private String userName;
    private String productName;
}
