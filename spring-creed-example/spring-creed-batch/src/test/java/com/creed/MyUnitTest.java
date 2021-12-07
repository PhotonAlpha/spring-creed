package com.creed;

import com.creed.dto.Employee;
import lombok.extern.slf4j.Slf4j;
import org.beanio.BeanReader;
import org.beanio.BeanWriter;
import org.beanio.StreamFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * @className: MyUnitTest
 * @author: Ethan
 * @date: 7/12/2021
 **/
public class MyUnitTest {
    public static final Logger log = LoggerFactory.getLogger(MyUnitTest.class);
    @Test
    void testInputOutput() throws IOException {
        InputStream inputStream = new ClassPathResource("beanio/bean-mapping.xml").getInputStream();
        StreamFactory factory = StreamFactory.newInstance();
        factory.load(inputStream);

        // InputStream input = new PathResource("/workspace/a.employee.txt").getInputStream();
        InputStream input = new ClassPathResource("a.employee.txt").getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
        BeanReader in = factory.createReader("employeeFile", bufferedReader);

        ClassPathResource output = new ClassPathResource("out");
        BeanWriter out = factory.createWriter("employeeFileOut", new File("D:\\workspace\\source\\gradle_workspace\\spring-creed\\spring-creed-example\\spring-creed-batch\\src\\main\\resources\\out/b.employee.txt"));

        Object record;
        var emp = new ArrayList<Employee>();
        while ((record = in.read()) != null) {
            String recordName = in.getRecordName();
            if ("header".equals(recordName)) {
                log.info("header:{}",record);
            } else if ("employee".equals(recordName)) {
                log.info("employee:{}", record);
                emp.add((Employee) record);
            } else if ("trailer".equals(recordName)) {
                log.info("trailer:{}",record);
            }
        }
        in.close();

        for (Employee employee : emp) {
            out.write(employee);
        }
        // Employee employee = new Employee();
        // employee.setRecordType("Detail");
        // employee.setFirstName("Joe");
        // employee.setLastName("Smith");
        // employee.setTitle("Developer");
        // employee.setSalary("75000");
        // employee.setHireDate(new Date());
        // out.write(employee);
        out.flush();
        out.close();
    }
}
