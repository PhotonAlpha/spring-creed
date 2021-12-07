package com.creed.handler;

import com.creed.dto.Employee;
import com.creed.dto.EmployeeHeader;
import com.creed.dto.EmployeeTrailer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

/**
 * @className: EmployeeProcess
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Slf4j
public class EmployeeProcess implements ItemProcessor<Object, Employee> {
    @Override
    public Employee process(Object o) throws Exception {
        log.info("EmployeeProcess {}", o);
        if (o instanceof Employee) {
            return (Employee) o;
        } else if (o instanceof EmployeeHeader) {
            log.info("get header:{}", o);
        } else if (o instanceof EmployeeTrailer) {
            log.info("get trailer:{}", o);
        }
        return null;
    }
}
