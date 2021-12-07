package com.creed.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * @className: JobType
 * @author: Ethan
 * @date: 7/12/2021
 **/
public enum JobType {
    JOB_BEAN1("myFirstJob");

    private String jobBeanName;

    JobType(String jobBeanName) {
        this.jobBeanName = jobBeanName;
    }

    public String getJobBeanName() {
        return jobBeanName;
    }

    public static Optional<JobType> findByName(String enumName) {
        return Stream.of(JobType.values()).filter(t -> StringUtils.equalsIgnoreCase(t.name(), enumName))
                .findAny();
    }
}
