package com.ethan.agent.util;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author EthanCao ethan.caoq@foxmail.com
 * @description spring-creed-pro
 * @date 3/12/24
 */
public class PrivateProxyFactory {
    private final String name;
    private final String[] urls;
    private static HashMap factories = new HashMap();

    private PrivateProxyFactory(String name, String[] urls, Properties props) {
        this.name = name;
        this.urls = urls;
    }

    public static synchronized PrivateProxyFactory registerFactory(String name, String[] urls, int connTimeoutSecs) throws MalformedURLException {
        return registerFactory(name, urls, connTimeoutSecs, (Properties)null);
    }

    public static synchronized PrivateProxyFactory registerFactory(String name, String[] urls, int connTimeoutSecs, Properties props) throws MalformedURLException {
        PrivateProxyFactory pf = (PrivateProxyFactory)factories.get(name);
        if (pf == null) {
            pf = new PrivateProxyFactory(name, urls, props);
            factories.put(name, pf);
        }
        return pf;
    }

    public static synchronized PrivateProxyFactory getInstance(String name) {
        return (PrivateProxyFactory)factories.get(name);
    }
}
