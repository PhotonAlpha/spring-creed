package com.ethan.common.utils.notification;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookInformation {
    private String category;
    private String author;
    private String title;
    private Double price;
}
