package com.ethan.agent.adaptor.resttemplate;

import net.bytebuddy.implementation.bind.annotation.Origin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 28/11/24
 */
public class RestTemplateInterceptor {
    public static final Logger log = LoggerFactory.getLogger(RestTemplateInterceptor.class);

    public static RestTemplate intercept(@Origin Method method) {
        log.info("replacing with Creed Buddy RestTemplate methodName:{}", method.getName());
        //创建一个临时的DataSource， 后续在 DevBuddyAgentBeanPostProcessor 中进行信息更正，用来解决初始化的问题
        return new RestTemplate();

    }
}
