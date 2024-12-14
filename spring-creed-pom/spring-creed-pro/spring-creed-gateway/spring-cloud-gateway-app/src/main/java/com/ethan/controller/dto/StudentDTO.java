package com.ethan.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
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
    private LocalDateTime timestamp = LocalDateTime.now();
    private String server;

    public StudentDTO(String id, String username, String gender, int age) {
        this.id = id;
        this.username = username;
        this.gender = gender;
        this.age = age;
    }
}
