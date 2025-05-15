package com.creed.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.beanio.InvalidRecordException;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @className: AbstractFileVerificationSkipper
 * @author: Ethan
 * @date: 7/12/2021
 **/
@Slf4j
public class AbstractFileVerificationSkipper implements SkipPolicy {
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    @Override
    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
        if (t instanceof InvalidRecordException) {
            InvalidRecordException recordException = (InvalidRecordException) t;
            String originalText = recordException.getRecordContext().getRecordText() + System.lineSeparator();
            String errorFileName = String.format("none_stand_error_%s.txt", LocalDateTime.now().format(DTF));
            String path = new ClassPathResource("beanio/bean-mapping.xml").getPath();
            path = StringUtils.substringBeforeLast(path, File.pathSeparator);
            try {
                Files.writeString(Path.of(path, errorFileName), originalText, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                log.error("writing encounter error", e);
            }
            return true;
        }
        return false;
    }
}
