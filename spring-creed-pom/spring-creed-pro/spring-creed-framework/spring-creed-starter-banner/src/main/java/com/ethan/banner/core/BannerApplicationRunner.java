package com.ethan.banner.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.util.ClassUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BannerApplicationRunner implements ApplicationRunner, InitializingBean, DisposableBean {
    private ExecutorService executor;
    private static final Logger log = LoggerFactory.getLogger(BannerApplicationRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        executor.execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);// 延迟 1 秒，保证输出到结尾
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            /* log.info("\n----------------------------------------------------------\n\t" +
                            "项目启动成功！\n\t" +
                            "接口文档: \t{} \n\t" +
                            "开发文档: \t{} \n\t" +
                            "视频教程: \t{} \n\t" +
                            "源码解析: \t{} \n" +
                            "----------------------------------------------------------",
                    "",
                    "",
                    "",
                    ""); */

            // 数据报表
            if (isNotPresent("com.ethan.framework.security.config.SecurityConfiguration")) {
                System.out.println("[报表模块 ethan-module-visualization - 已禁用]");
            }
            // 工作流
            if (isNotPresent("com.ethan.framework.flowable.config.FlowableConfiguration")) {
                System.out.println("[工作流模块 ethan-module-bpm - 已禁用]");
            }
        });
    }

    private static boolean isNotPresent(String className) {
        return !ClassUtils.isPresent(className, ClassUtils.getDefaultClassLoader());
    }

    @Override
    public synchronized void destroy() throws Exception {
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (executor != null) {
            executor.shutdownNow();
        }
        executor = Executors.newSingleThreadExecutor();
    }
}
