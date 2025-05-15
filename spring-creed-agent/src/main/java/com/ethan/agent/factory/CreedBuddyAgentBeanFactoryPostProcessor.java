package com.ethan.agent.factory;

import com.ethan.agent.factory.filter.CreedBuddyRequestFilter;
import com.ethan.agent.factory.filter.RouteRegisterEndpointFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.util.List;
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
        if (Objects.isNull(beanFactory.getParentBeanFactory())) {
            //项目启动之前 BeanDefinitionRegistryPostProcessor已经注册了某些bean,这里需要强制取消注册
            deRegisterBeanDefinition(beanFactory);

            //检查初始化的时候根容器，跳过下面的bean检查
            return;
        }

        /* 在BeanFactoryPostProcessor 注册自定义filter*/
        // check is camel project
       /*  if ( beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            BeanDefinition definition = BeanDefinitionBuilder
                    .genericBeanDefinition(RouteRegisterEndpointFilter.class)
                    .getBeanDefinition();
            registry.registerBeanDefinition("routeRegisterEndpointFilter", definition);
        } */
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public static final Map<String, String> CONDITION_BEAN_ADAPTOR = Map.of();
    public void deRegisterBeanDefinition(ConfigurableListableBeanFactory beanFactory) {
        log.info("@.@[deRegisterBeanDefinition]@.@");
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        /* for (String beanDefinitionName : beanDefinitionNames) {
            log.debug("@.@[beanDefinitionName:{}]@.@", beanDefinitionName);
        } */
        for (Map.Entry<String, String> entry : CreedBuddyAgentBeanDefinitionRegistryPostProcessor.CONDITION_BEAN_ADAPTOR.entrySet()) {
            // 检测是否存在 BeanDefinition, 删除该bean的初始化
            // beanFactory.getB
            List<String> beanDefinition = Stream.of(beanDefinitionNames)
                    .filter(name -> StringUtils.equalsIgnoreCase(name, entry.getKey())).toList();
            log.debug("@.@[checking beanDefinition:{}]@.@", beanDefinition);
            if (!CollectionUtils.isEmpty(beanDefinition) && beanFactory instanceof BeanDefinitionRegistry registry) {
                log.info("@.@[existing beanDefinition:{}]@.@", beanDefinition);
                for (String name : beanDefinition) {
                    registry.removeBeanDefinition(name);
                }
            }
        }
    }
}
