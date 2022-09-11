/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.beanio.process;

import com.ethan.beanio.vo.StudentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/25/2022 4:50 PM
 */
public class FileProcess implements ItemProcessor<Object, StudentDTO> {
    private static final Logger log = LoggerFactory.getLogger(FileProcess.class);
        @Override
        public StudentDTO process(Object o) throws Exception {
            log.info("dtl:{}", o);
            if (o instanceof StudentDTO) {
                StudentDTO detailVo = (StudentDTO) o;
                return detailVo;
            } else {
                return null;
            }
        }
}
