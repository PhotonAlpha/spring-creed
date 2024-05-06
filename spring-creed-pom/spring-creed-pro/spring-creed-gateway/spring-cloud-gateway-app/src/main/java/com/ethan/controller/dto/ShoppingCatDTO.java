package com.ethan.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 9/4/24
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingCatDTO {
    private String id;
    private String username;
    private String product;
    private Integer amount;
}
