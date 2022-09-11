package com.ethan.beanio.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.beanio.InvalidRecordException;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.MultiResourceItemReader;


@Slf4j
public abstract class AbstractFileVerificationSkipper implements SkipPolicy {
    private Date inputDate;
    private String archivePath;
    private MultiResourceItemReader multiResourceItemReader;


    public AbstractFileVerificationSkipper(Date inputDate, String archivePath, MultiResourceItemReader multiResourceItemReader) {
        this.inputDate = inputDate;
        this.archivePath = archivePath;
        this.multiResourceItemReader = multiResourceItemReader;
    }

    @Override
    public boolean shouldSkip(Throwable throwable, int i) throws SkipLimitExceededException {
        if (throwable instanceof InvalidRecordException) {
            InvalidRecordException ire = (InvalidRecordException) throwable;
            String filename = multiResourceItemReader.getCurrentResource().getFilename();
            String originalText = ire.getRecordContext().getRecordText() + System.lineSeparator();
            int lineNumber = ire.getRecordContext().getLineNumber();
            inputDate = new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(inputDate.toInstant(), ZoneId.systemDefault());

            log.error("An error occured while processing the {} at line: {} original txt:{}", inputDate, lineNumber, originalText);
            log.info("---> file name:{}", filename);
            String errorFileName = String.format("%s_error_%s.txt", "a", "b");
            try {
                Files.writeString(Path.of(archivePath, errorFileName), originalText, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error("writeString encounter error:", e);
            }
            return true;
        }
        return false;
    }
}
