package com.ethan.controller.settlement.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSettlementVo {
    private String userName;
    private String orderNo;
    private boolean success;
}
