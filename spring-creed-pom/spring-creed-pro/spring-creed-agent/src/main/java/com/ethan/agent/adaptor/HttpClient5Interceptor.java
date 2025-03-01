package com.ethan.agent.adaptor;

import com.ethan.agent.exception.CreedBuddyException;
import net.bytebuddy.implementation.bind.annotation.Origin;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.lang.reflect.Method;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 19/11/24
 */
public class HttpClient5Interceptor {
    public static final Logger log = LoggerFactory.getLogger(HttpClient5Interceptor.class);

    public static SSLContext intercept(@Origin Method method) {
        log.info("replacing with Creed Buddy SSLContext OnMethodEnter methodName:{}", method.getName());
        try {
            return SSLContexts.custom()
                    .build();
        } catch (Exception e) {
            throw new CreedBuddyException(e);
        }
    }
    public static SSLConnectionSocketFactory interceptConnection(@Origin Method method) {
        log.info("replacing with Creed Buddy SSLConnectionSocketFactory OnMethodEnter methodName:{}", method.getName());
        return connectionSocketFactory(method);
    }

    public static SSLConnectionSocketFactory connectionSocketFactory(Method method) {
        SSLContext sslContext = intercept(method);
        final HostnameVerifier verifier = NoopHostnameVerifier.INSTANCE;
        return new SSLConnectionSocketFactory(sslContext, verifier);
    }


    /**
     * 拦截原有方法，并调用原方法的参数
     */
    /* @RuntimeType
    public static void intercept(@Origin Method method, @AllArguments Object[] args, @SuperCall Callable<Void> zuper) throws Exception {
        zuper.call();
    } */

}
