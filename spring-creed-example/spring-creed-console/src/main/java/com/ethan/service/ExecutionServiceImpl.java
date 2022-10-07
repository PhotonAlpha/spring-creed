/**
 * Copyright the original author or authors.
 *
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 */
package com.ethan.service;

import com.ethan.dto.CommandReqDto;
import com.ethan.vo.R;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @description: vue-console
 * @author: EthanCao
 * @email: ethan.caoq@foxmail.com
 * @date: 9/15/2022 12:17 PM
 */
@Service
public class ExecutionServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(ExecutionServiceImpl.class);
    public R executeCommand(CommandReqDto dto) {
        log.info("get request:{}", dto);

        // List<String> commands = Optional.ofNullable(dto)
        //         .map(CommandReqDto::getCommand)
        //         .map(c -> StringUtils.split(c, StringUtils.LF))
        //         .map(Arrays::asList)
        //         .orElse(Collections.emptyList());
        String command = Optional.ofNullable(dto)
                .map(CommandReqDto::getCommand)
                .orElse(StringUtils.EMPTY);
        String output = exec(command);

        List<String> outputList = Optional.of(output)
                .map(c -> StringUtils.split(c, StringUtils.LF))
                .map(Arrays::asList)
                .orElse(Collections.emptyList());
        return R.success(outputList);
    }

    @Deprecated
    private String exec(String command) {
        StringBuilder sb = new StringBuilder();
        // StringBuilder errorSb = new StringBuilder();
        ExecutorService pool = Executors.newSingleThreadExecutor();
        try {
            Process p = Runtime.getRuntime().exec(command);
            /* 为"错误输出流"单独开一个线程读取之,否则会造成标准输出流的阻塞 */
            // Thread t = new Thread(new InputStreamRunnable(p.getErrorStream(), "ErrorStream", errorSb));
            // t.start();
            ProcessReadTask task = new ProcessReadTask(p.getErrorStream());
            Future<List<String>> future = pool.submit(task);

            /* "标准输出流"就在当前方法中读取 */
            try (InputStreamReader inputStreamReader = new InputStreamReader(p.getInputStream());
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    log.info("line:{}", line);
                    sb.append(line);
                    sb.append("\n");
                }
                int exitCode = p.waitFor();
                log.info("exitcode:"+exitCode);
            } finally {
                p.destroy();
            }

            List<String> strings = future.get(5, TimeUnit.SECONDS);
            sb.append(String.join("\n", strings));
        } catch (Exception e) {
            log.error("exception occur ",e);
            sb.append(e.getMessage());
            sb.append("\n");
        } finally {
            pool.shutdown();
        }

        return sb.toString();
    }
    private String exec2(List<String> commands) {
        StringBuilder sb = new StringBuilder();

        // ExecutorService pool = Executors.newSingleThreadExecutor();
        ProcessBuilder processBuilder = new ProcessBuilder("git --version");
        processBuilder.redirectErrorStream(true);
        // Run this on Windows, cmd, /c = terminate after this run
        // for (String c : commands) {
        //     processBuilder.command(c);
        // }
        try {
            Process p = processBuilder.start();

            // ProcessReadTask task = new ProcessReadTask(p.getErrorStream());
            // Future<List<String>> future = pool.submit(task);

            /* "标准输出流"就在当前方法中读取 */

            try (InputStreamReader inputStreamReader = new InputStreamReader(p.getInputStream());
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
                int exitCode = p.waitFor();
                log.info("Exited with error code : " + exitCode);
            }

        } catch (Exception e) {
            log.error("exception occur ",e);
            sb.append(e.getMessage());
            sb.append("/n");
        } finally {
            // pool.shutdown();
        }

        return sb.toString();
    }

    @Deprecated
    static class InputStreamRunnable implements Runnable {
        private BufferedReader bReader = null;
        private StringBuilder errorSb;
        InputStreamRunnable(InputStream is, String type, StringBuilder errorSb) {
            try {
                bReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(is), "UTF-8"));
                this.errorSb = errorSb;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        public void run() {
            String line;
            try {
                while ((line = bReader.readLine()) != null) {
                    //System.out.println("---->"+String.format("%02d",num++)+" "+line);
                    this.errorSb.append(line);
                    this.errorSb.append("/n");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    bReader.close();
                } catch (IOException e) {
                    log.error("close failure", e);
                }

            }
        }
    }

    private static class ProcessReadTask implements Callable<List<String>> {
        private InputStream inputStream;
        public ProcessReadTask(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        @Override
        public List<String> call() {
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.toList());
        }
    }
}
