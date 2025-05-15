package com.ethan.system.pdf.controller.dto;

import lombok.Data;

import java.util.List;

@Data
public class Company {
    private String referenceNo="abc-xxx-xxxxx-xx";
    private String mobileCountryCode = "86";
    private String mobileNo = "0511-21212121";
    private String companyName = "LTD. ptd.";
    private String address = "xxx road xxx stress 123#321";
    private List<Employee> employees;
}
