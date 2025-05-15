package com.creed.handler;

import com.creed.dto.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

import java.util.List;

/**
 * @className: EmployeeWriter
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Slf4j
public class EmployeeWriterListener implements ItemWriteListener<Employee> {
    @Override
    public void beforeWrite(Chunk<? extends Employee> list) {
        log.info("EmployeeWriterListener beforeWrite:{}", list);
    }

    @Override
    public void afterWrite(Chunk<? extends Employee> list) {
        log.info("EmployeeWriterListener afterWrite:{}", list);
    }

    @Override
    public void onWriteError(Exception e, Chunk<? extends Employee> list) {
        log.info("EmployeeWriterListener onWriteError:{} exception:{}", list, e);
    }
}
