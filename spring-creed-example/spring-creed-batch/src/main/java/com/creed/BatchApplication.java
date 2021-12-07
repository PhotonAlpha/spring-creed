package com.creed;

import com.creed.constant.JobType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Date;
import java.util.Optional;

/**
 * @className: BatchApplication
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Slf4j
@SpringBootApplication
public class BatchApplication implements CommandLineRunner {
    private ApplicationContext applicationContext;
    private JobLauncher jobLauncher;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder().sources(BatchApplication.class).web(WebApplicationType.NONE).run(args);
        System.exit(SpringApplication.exit(context));
    }

    @Override
    public void run(String... args) throws Exception {
        String jobName = "JOB_BEAN1";
        // if (args.length > 0) {
        //     jobName = args[0];
        // }
        Optional<JobType> jobTypeOptional = JobType.findByName(jobName);
        if (jobTypeOptional.isPresent()) {
            String jobBeanName = jobTypeOptional.get().getJobBeanName();
            log.info("Batch Start with {}", jobName);
            Job job = applicationContext.getBean(jobBeanName, Job.class);
            JobExecution run = jobLauncher.run(job, jobParameters());
            log.info("status:{}", run.getExitStatus());
        }
    }

    private JobParameters jobParameters() {
        return new JobParametersBuilder().addDate("date", new Date()).toJobParameters();
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    @Autowired
    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }
}
