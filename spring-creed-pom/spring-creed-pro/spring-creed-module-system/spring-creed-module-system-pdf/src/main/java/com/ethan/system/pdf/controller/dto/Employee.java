/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 */

package com.ethan.system.pdf.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Employee {

    private String firstName;
    private String lastName;
    private String email;
    private String mobileCountryCode;
    private String mobileNo;
}
