package com.creed.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @className: ArchiveTasklet
 * @author: Ethan
 * @date: 14/12/2021
 **/
@Component
@Slf4j
public class ArchiveTasklet implements Tasklet {
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = null;
        try {
            resources = patternResolver.getResources("classpath*:*.employee.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("resources size:{}", resources.length);
        if (resources.length > 0) {
            File destDir = new File("/tmp/archive");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            Path destPath = destDir.toPath();
            for (Resource resource : resources) {
                File file = resource.getFile();
                String fileName = String.format("%s_%s", file.getName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
                // Files.copy(Paths.get(file.toURI()), destPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                log.info("###Archive file to path {} ###", destPath);
                // Files.delete(file.toPath());
            }
        }
        return RepeatStatus.FINISHED;
    }
}
