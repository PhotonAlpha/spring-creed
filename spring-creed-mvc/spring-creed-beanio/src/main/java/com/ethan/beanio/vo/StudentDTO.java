package com.ethan.beanio.vo;

import lombok.Data;
import lombok.ToString;
import org.springframework.batch.item.ResourceAware;
import org.springframework.core.io.Resource;

@Data
@ToString
public class StudentDTO implements ResourceAware {
    private String emailAddress;
    private String name;
    private String purchasedPackage;
    private CustomerDTO customer;

    @Override
    public void setResource(Resource resource) {

    }
}
