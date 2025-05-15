package com.creed.config;

import com.creed.constant.IStream;
import com.creed.dto.Employee;
import com.creed.handler.AbstractFileVerificationSkipper;
import com.creed.handler.ArchiveTasklet;
import com.creed.handler.EmployeeProcess;
import com.creed.handler.EmployeeProcessListener;
import com.creed.handler.EmployeeWriter;
import com.creed.handler.EmployeeWriterListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.beanio.StreamFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.PlatformTransactionManager;

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

    private final JobRepository jobRepository;
    private final PlatformTransactionManager policyTransactionManager;

    public BatchConfiguration(JobRepository jobRepository, PlatformTransactionManager policyTransactionManager) {
        this.jobRepository = jobRepository;
        this.policyTransactionManager = policyTransactionManager;
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
        var builder = new JobBuilder("myFirstJob", jobRepository);
        return builder
                .incrementer(new RunIdIncrementer())
                // .start(myFirstStep())

                // .flow(myFirstStep()).on(ExitStatus.FAILED.getExitCode()).to(failureTasklet())
                // .from(myFirstStep()).on("*").to(archiveTasklet()).end()

                .flow(myFirstStep()).on("*").to(archiveTasklet()).end()
                .build();
    }

    @Bean
    public Step myFirstStep() {
        return new StepBuilder("myFirstStep", jobRepository).<Object, Employee>chunk(500, policyTransactionManager)
                .reader(multiResourceItemReader())
                .faultTolerant().skipPolicy(skipPolicy())
                .processor(itemProcessor())
                .listener(itemProcessorListener())
                .writer(itemWriter())
                .listener(itemWriteListener())
                .build();
    }

    @Autowired
    private ArchiveTasklet archiveTasklet;
    @Bean
    public Step archiveTasklet() {
        return new StepBuilder("archiveTasklet", jobRepository)
                .tasklet(archiveTasklet, policyTransactionManager)
                .build();
    }

    /**
     * TODO
     * public BeanIOFlatFileItemReader<Object> beanReader(IStream iStream) {
     * BeanIOFlatFileItemReader<Object> itemReader = new BeanIOFlatFileItemReader<>();
     * itemReader.setStreamFactory(streamFactory());
     * itemReader.setStreamName(iStream.streamName());
     * return itemReader;
     * }
     */
    @SneakyThrows
    private ResourceAwareItemReaderItemStream<?> beanReader(IStream iStream, @Value("#{jobParameters['inputFilePath']}") String inputFilePath) {
        var resources = new PathMatchingResourcePatternResolver().getResources("file:" + inputFilePath + "/*.csv");
        MultiResourceItemReader<Object> reader = new MultiResourceItemReader<>();
        reader.setResources(resources);

        FlatFileItemReader<Object> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setName("CEW-CSV-Reader");
        flatFileItemReader.setLinesToSkip(1);
        // flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
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
        itemReader.setDelegate(beanReader(IStream.defaultStream(), "test"));
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
