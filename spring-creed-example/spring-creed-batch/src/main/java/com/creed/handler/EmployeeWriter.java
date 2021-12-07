package com.creed.handler;

import com.creed.dto.Employee;
import lombok.extern.slf4j.Slf4j;
import org.beanio.StreamFactory;
import org.beanio.spring.BeanIOFlatFileItemWriter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * @className: EmployeeWriter
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Slf4j
public class EmployeeWriter implements ItemWriter<Employee>, Closeable {
    private StreamFactory streamFactory;

    @Autowired
    public void setStreamFactory(StreamFactory streamFactory) {
        this.streamFactory = streamFactory;
    }

    @Override
    public void close() throws IOException {
        log.info("...EmployeeWriter closing...");
    }

    @Override
    public void write(List<? extends Employee> items) {
        BeanIOFlatFileItemWriter<Object> itemWriter = null;
        try {
            itemWriter = new BeanIOFlatFileItemWriter<>();
            itemWriter.setStreamFactory(streamFactory);
            itemWriter.setStreamName("employeeFileOut");
            itemWriter.setAppendAllowed(true);
            itemWriter.setResource(new PathResource("D:\\workspace\\source\\gradle_workspace\\spring-creed\\spring-creed-example\\spring-creed-batch\\src\\main\\resources\\out\\c.employee.txt"));
            // itemWriter.setResource(new ClassPathResource("out/c.employee.txt"));
            itemWriter.setTransactional(false);
            itemWriter.open(new ExecutionContext());
            itemWriter.afterPropertiesSet();
            itemWriter.write(items);
        } catch (Exception e) {
            log.error("writing encounter exception", e);
        } finally {
            if (itemWriter != null) {
                itemWriter.close();
            }
        }
    }
}
