package com.ethan.agent.factory;

import com.ethan.agent.factory.filter.CreedBuddyRequestFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 可以用来修改 DataSource 的配置，例如修改mysql的配置,读取配置文件中的db config https://developer.aliyun.com/article/632081 https://www.cnblogs.com/youzhibing/p/10559337.html
 * BeanFactoryPostProcessor 主要是用来修改已经定义的 bean 定义，而不是注册新的 bean。
 *
 * @author EthanCao
 * @description spring-creed-agent
 * @date 20/11/24
 */
@Slf4j
public class CreedBuddyAgentBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered {
    private final ApplicationContext applicationContext;

    public CreedBuddyAgentBeanFactoryPostProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.setProperty("spring.main.allow-bean-definition-overriding", "true");
        System.setProperty("logging.config", "");
        System.setProperty("logging.file.name", "./logs/${logging.instance:${spring.application.name:GEBNGCUSG01}}.log");

        if (Objects.isNull(beanFactory.getParentBeanFactory())) {
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
            //项目启动之前 初始化某些特定的bean
            registerBeanDefinition(beanDefinitionNames, beanFactory);

            //检查初始化的时候根容器，跳过下面的bean检查
            return;
        }
        // log.info("调用UserFactoryPostProcessor的postProcessBeanFactory方法");
        // 修改 filter ,跳过验证
        try {
            String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

            if (beanDefinitionNames != null && beanDefinitionNames.length > 0) {
                var tokenFilter = Stream.of(beanDefinitionNames)
                        .filter(name -> name.endsWith("TokenFilter")).toList();
                for (String filterName : tokenFilter) {
                    BeanDefinition tokeFilterBeanDefinition = beanFactory.getBeanDefinition(filterName);
                    tokeFilterBeanDefinition.setBeanClassName(CreedBuddyRequestFilter.class.getName());
                }
            }
        } catch (BeansException e) {
            // skip handle
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public static final Map<String, String> CONDITION_BEAN_ADAPTOR = Map.of();
    public void registerBeanDefinition(String[] beanDefinitionNames, ConfigurableListableBeanFactory beanFactory) {
        for (Map.Entry<String, String> entry : CONDITION_BEAN_ADAPTOR.entrySet()) {
            var beanDefinition = Stream.of(beanDefinitionNames)
                    .filter(name -> name.endsWith(entry.getKey())).toList();
            if (CollectionUtils.isEmpty(beanDefinition)) {
                try {
                    if (beanFactory instanceof BeanDefinitionRegistry registry) {
                        Class<?> paramConfigClazz = Class.forName(entry.getValue());
                        var definition = BeanDefinitionBuilder.genericBeanDefinition(paramConfigClazz).getBeanDefinition();
                        registry.registerBeanDefinition(entry.getKey(), definition);
                        log.debug("registerBeanDefinition:{}", definition);
                    }
                } catch (ClassNotFoundException e) {
                    // ignore , if not MS, this class not existing
                    log.warn("{} not exist", entry.getValue());
                }

            }
        }
    }
}
