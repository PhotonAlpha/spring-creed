package com.ethan.common.utils;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.Properties;

/**
 * 解决读取多个路径下的 messagesource 问题
 * https://stackoverflow.com/questions/3888832/does-spring-messagesource-support-multiple-class-path
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 13/1/25
 */
public class SmReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
    private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private static final String PROPERTIES_SUFFIX = ".properties";
    @Override
    protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
        if (filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX)) {
            return refreshClassPathProperties(filename, propHolder);
        } else {
            return super.refreshProperties(filename, propHolder);
        }
    }
    private PropertiesHolder refreshClassPathProperties(String filename, PropertiesHolder propHolder) {
        Properties properties = new Properties();
        long lastModified = -1;
        try {
            Resource[] resources = resolver.getResources(filename + PROPERTIES_SUFFIX);
            for (Resource resource : resources) {
                String sourcePath = resource.getURI().toString().replace(PROPERTIES_SUFFIX, "");
                PropertiesHolder holder = super.refreshProperties(sourcePath, propHolder);
                properties.putAll(holder.getProperties());
                if (lastModified < resource.lastModified())
                    lastModified = resource.lastModified();
            }
        } catch (IOException ignored) {
        }
        return new PropertiesHolder(properties, lastModified);
    }
}
