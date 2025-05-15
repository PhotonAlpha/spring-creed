package com.ethan.controller.settlement.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettlementReqDto {
    private String userName;
    private String productName;
    private Long stock;
}
