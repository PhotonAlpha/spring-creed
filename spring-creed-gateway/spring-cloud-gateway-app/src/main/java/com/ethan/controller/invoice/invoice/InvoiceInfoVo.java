package com.ethan.controller.invoice.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceInfoVo {
    private String productName;
    private String invoiceRefNumber;
    private String type;
    private String address;
}
