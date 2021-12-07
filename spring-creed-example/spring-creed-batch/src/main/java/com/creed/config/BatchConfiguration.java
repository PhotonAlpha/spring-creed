package com.creed.config;

import com.creed.constant.IStream;
import com.creed.handler.AbstractFileVerificationSkipper;
import com.creed.handler.EmployeeProcess;
import com.creed.handler.EmployeeProcessListener;
import com.creed.handler.EmployeeWriter;
import com.creed.handler.EmployeeWriterListener;
import lombok.extern.slf4j.Slf4j;
import org.beanio.StreamFactory;
import org.beanio.spring.BeanIOFlatFileItemReader;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

/**
 * @className: BatchConfiguration
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Slf4j
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    private StepBuilderFactory stepBuilderFactory;

    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    public void setStepBuilderFactory(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Autowired
    public void setJobBuilderFactory(JobBuilderFactory jobBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
    }

    @Bean
    public StreamFactory streamFactory() {
        StreamFactory streamFactory = StreamFactory.newInstance();
        streamFactory.loadResource("beanio/bean-mapping.xml");
        return streamFactory;
    }

    @Bean
    public Job myFirstJob() {
        log.info("...myFirstJob...");
        return jobBuilderFactory.get("myFirstJob")
                .incrementer(new RunIdIncrementer())
                .start(myFirstStep())
                .build();
    }

    @Bean
    public Step myFirstStep() {
        return stepBuilderFactory.get("myFirstStep").chunk(500)
                .reader(multiResourceItemReader())
                .faultTolerant().skipPolicy(skipPolicy())
                .processor(itemProcessor())
                .listener(itemProcessorListener())
                .writer(itemWriter())
                .listener(itemWriteListener())
                .build();
    }

    public BeanIOFlatFileItemReader<Object> beanReader(IStream iStream) {
        BeanIOFlatFileItemReader<Object> itemReader = new BeanIOFlatFileItemReader<>();
        itemReader.setStreamFactory(streamFactory());
        itemReader.setStreamName(iStream.streamName());
        return itemReader;
    }
    @Bean
    public ItemReader<Object> multiResourceItemReader() {
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        try {
            resources = patternResolver.getResources("classpath*:*.employee.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("resources size:{}", resources.length);
        MultiResourceItemReader<Object> itemReader = new MultiResourceItemReader<>();
        itemReader.setResources(resources);
        itemReader.setDelegate(beanReader(IStream.defaultStream()));
        return itemReader;
    }

    @Bean
    public SkipPolicy skipPolicy() {
        return new AbstractFileVerificationSkipper();
    }
    @Bean
    public ItemProcessor itemProcessor() {
        return new EmployeeProcess();
    }
    @Bean
    public ItemProcessListener itemProcessorListener() {
        return new EmployeeProcessListener();
    }
    @Bean
    public ItemWriter itemWriter() {
        return new EmployeeWriter();
    }
    @Bean
    public ItemWriteListener itemWriteListener() {
        return new EmployeeWriterListener();
    }
}
