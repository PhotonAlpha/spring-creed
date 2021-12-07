package com.creed.dto;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @className: Employee
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Data
@ToString
public class Employee {
    private String recordType;
    private String firstName;
    private String lastName;
    private String title;
    private String salary;
    private Date hireDate;
    private String endType;
}
