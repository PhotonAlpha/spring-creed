package com.creed.handler;

import com.creed.dto.Employee;
import com.creed.dto.EmployeeHeader;
import com.creed.dto.EmployeeTrailer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.item.ItemProcessor;

/**
 * @className: EmployeeProcess
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Slf4j
public class EmployeeProcessListener implements ItemProcessListener<Object, Employee> {
    @Override
    public void beforeProcess(Object item) {
        log.info("EmployeeProcessListener beforeProcess:{}", item);
    }

    @Override
    public void afterProcess(Object item, Employee result) {
        log.info("EmployeeProcessListener afterProcess:{} result:{}", item, result);
    }

    @Override
    public void onProcessError(Object item, Exception e) {
        log.info("EmployeeProcessListener onProcessError:{} exception:{}", item, e);
    }
}
