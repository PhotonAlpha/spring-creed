package com.ethan.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 9/4/24
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {
    private String id;
    private String username;
    private String gender;
    private int age;
}
