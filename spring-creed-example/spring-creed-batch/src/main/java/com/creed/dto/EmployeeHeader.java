package com.creed.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @className: Header
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Data
@ToString
public class EmployeeHeader {
    private String recordType;
    private Date fileDate;
}
