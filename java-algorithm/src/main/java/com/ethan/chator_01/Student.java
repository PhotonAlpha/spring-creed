package com.ethan.chator_01;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @className: Student
 * @author: Ethan
 * @date: 18/6/2021
 **/
@Component
@Data
public class Student {
    private String name;
    private Integer age;
}
