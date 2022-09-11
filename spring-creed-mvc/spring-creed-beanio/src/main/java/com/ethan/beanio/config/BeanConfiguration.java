/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.beanio.config;

import com.ethan.beanio.handler.FileVerificationSkipper;
import com.ethan.beanio.process.FileProcess;
import com.ethan.beanio.vo.StudentDTO;
import com.ethan.beanio.writter.FileWriter;
import lombok.SneakyThrows;
import org.beanio.StreamFactory;
import org.beanio.spring.BeanIOFlatFileItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

/**
 * @description: spring-creed
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 8/25/2022 4:37 PM
 */
@Configuration
public class BeanConfiguration {
    private static final Logger log = LoggerFactory.getLogger(BeanConfiguration.class);
    @Value("${batch.filepath:file:/inbound/*.TXT}")
    private String filePath;

    /**
     * see {@link  BeanIoTest}
     * @return
     */
    @Bean
    public ItemReader<Object> multiResourceItemReader() {
        Resource[] resources = null;
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        try {
            resources = patternResolver.getResources(filePath);
        } catch (IOException e) {
            log.error("Pattern Resolver Error: ",  e);
        }
        log.info("resources size:{}", resources.length);
        MultiResourceItemReader<Object> resouceItenReader = new MultiResourceItemReader<>();
        resouceItenReader.setResources(resources);
        resouceItenReader.setDelegate(beanReader());
        return resouceItenReader;
    }

    public BeanIOFlatFileItemReader<Object> beanReader() {
        BeanIOFlatFileItemReader<Object> beanReader = new BeanIOFlatFileItemReader<>();
        beanReader.setStreamFactory(streamFactory());
        beanReader.setStreamName("inputMapping");
        return beanReader;
    }

    @Bean
    public StreamFactory streamFactory() {
        StreamFactory streamFactory = StreamFactory.newInstance();
        streamFactory.loadResource("beanio/input-mapping.xml");
        return streamFactory;
    }

    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private FileVerificationSkipper fileVerificationSkipper;

    @Bean
    public ItemProcessor<Object, StudentDTO> fileProcess() {
        return new FileProcess();
    }
    @Bean
    @StepScope
    @SneakyThrows
    public ItemWriter fileWriter() {
        return new FileWriter();
    }


    @Bean
    public Step studentStep() {
        log.info("......");
        return stepBuilderFactory.get("studentStep").<Object, Object>chunk(500)
                .reader(multiResourceItemReader())
                .faultTolerant().skipPolicy(fileVerificationSkipper)
                // .listener(sweeperReadListener())
                .processor(fileProcess())
                // .writer(writer())
                .writer(fileWriter())
                // .faultTolerant().skip(Exception.class).skipLimit(SKIP_LIMIT)
                .build();
    }
}
