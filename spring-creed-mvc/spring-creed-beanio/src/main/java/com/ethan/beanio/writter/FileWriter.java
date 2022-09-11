/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.beanio.writter;

import com.ethan.beanio.vo.StudentDTO;
import org.beanio.StreamFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/25/2022 4:52 PM
 */
public class FileWriter implements ItemWriter<StudentDTO>, Closeable {
    private static final Logger log = LoggerFactory.getLogger(FileWriter.class);
    @Autowired
    private StreamFactory streamFactory;

    private Map<String, String> currency;

    @Value("${batch.file.archive:/staging/inbound/tranresp}")
    protected String errorArchivePath;

    @Override
    public void write(List<? extends StudentDTO> list) throws Exception {
        log.info("list:{}", list.size());
        if (currency == null || currency.isEmpty()) {
            beforeStep(null);
        }
        List<StudentDTO> notFoundList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(notFoundList)) {
            // String filename = Optional.ofNullable(notFoundList).orElse(Collections.emptyList())
            //         .stream().findAny()
            //         .map(StudentDTO::getCurrentResource)
            //         .map(Resource::getFilename).orElse("");
            String filename = "1";
            LocalDateTime inputDate = LocalDateTime.now();


            log.error("An error occured while processing the {} notFoundList size:{}", inputDate, notFoundList.size());
            log.info("---> file name:{}", filename);
            // if (StringUtils.isBlank(filename)) {
            //     log.error("could not find resource:{}", notFoundList);
            //     return;
            // }
            String errorFileName = String.format("%s_error_%s.txt", "a", "b");
            // beanIOUtils.beanIOFlatFileItemWriter(streamFactory, "outputMapping",
            //         Path.of(errorArchivePath, errorFileName), notFoundList);
        }
    }

    @PreDestroy
    @Override
    public void close() {
        log.info("......FileWriter Closeing......");
    }

    public void setErrorArchivePath(String errorArchivePath) {
        this.errorArchivePath = errorArchivePath;
    }

    /**
     * Execute this before executing write()
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        log.info("......Writer Before Start......");
    }
}
