package com.ethan.beanio.handler;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@StepScope
public class FileVerificationSkipper extends AbstractFileVerificationSkipper {

    public FileVerificationSkipper(
            @Value("#{jobParameters['date']}") Date inputDate,
            @Value("${batch.file.archive:/staging/inbound/tranresp}") String errorArchivePath,
            @Qualifier("multiResourceItemReader") MultiResourceItemReader multiResourceItemReader
    ) {
        super(inputDate, errorArchivePath, multiResourceItemReader);
    }
}
