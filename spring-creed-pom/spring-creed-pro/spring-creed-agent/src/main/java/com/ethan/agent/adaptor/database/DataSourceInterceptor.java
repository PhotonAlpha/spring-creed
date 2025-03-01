package com.ethan.agent.adaptor.database;

import net.bytebuddy.implementation.bind.annotation.Origin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;
import java.lang.reflect.Method;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 19/11/24
 */
public class DataSourceInterceptor {
    public static final Logger log = LoggerFactory.getLogger(DataSourceInterceptor.class);

    public static DataSource intercept(@Origin Method method) {
        log.info("replacing with Creed Buddy DataSource methodName:{}", method.getName());
        //创建一个临时的DataSource， 后续在 DevBuddyAgentBeanPostProcessor 中进行信息更正，用来解决初始化的问题
        return DataSourceBuilder.create().build();

    }
}
