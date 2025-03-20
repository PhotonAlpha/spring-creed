package com.ethan.agent.factory;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.Ordered;

import java.util.Map;

/**
 * 可以用来修改 DataSource 的配置，例如修改mysql的配置,读取配置文件中的db config https://developer.aliyun.com/article/632081 https://www.cnblogs.com/youzhibing/p/10559337.html
 * BeanFactoryPostProcessor 主要是用来修改已经定义的 bean 定义，而不是注册新的 bean。
 *
 * @author EthanCao
 * @description spring-creed-agent
 * @date 20/11/24
 */
@Slf4j
public class CreedBuddyAgentBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, Ordered {

    public CreedBuddyAgentBeanDefinitionRegistryPostProcessor() {
    }
    public static final Map<String, String> CONDITION_BEAN_ADAPTOR = Map.of();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.info("@.@[postProcessBeanDefinitionRegistry]@.@");
        if (registry instanceof ConfigurableListableBeanFactory ) {
            ConfigurableListableBeanFactory factory = (ConfigurableListableBeanFactory) registry;
            for (Map.Entry<String, String> entry : CONDITION_BEAN_ADAPTOR.entrySet()) {
                try {
                    log.info("@.@[create instance:{}]@.@", entry.getValue());
                    Class<?> subClazz = Class.forName(entry.getValue());
                    Class<?> loaded = new ByteBuddy()
                            .subclass(subClazz)
                            .make()
                            .load(subClazz.getClassLoader())
                            .getLoaded();
                    Object instance = loaded.newInstance();
                    factory.registerSingleton(entry.getKey(), instance);
                    log.debug("@.@[registerBean key:{}]@.@", entry.getKey());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    log.warn("@.@[Class {} not exist]@.@", entry.getValue());
                } catch (Exception e) {
                    log.warn("@.@[Unknow Exception {}]@.@", e.getMessage());
                }

            }
        }
    }
    @Override
    public int getOrder() {
        return 0;
    }

}
