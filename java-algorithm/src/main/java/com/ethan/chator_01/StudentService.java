package com.ethan.chator_01;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @className: StudentService
 * @author: Ethan
 * @date: 18/6/2021
 **/
@Component
public class StudentService implements BeanNameAware, InitializingBean {
    @Autowired
    private Student student;

    private String beanName;
    private String studentName;
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
    public void print() {
        System.out.println(beanName);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.studentName = student.getName();
    }
}
