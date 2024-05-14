package org.example;

import java.lang.instrument.Instrumentation;

/**
 * @author EthanCao
 * @description spring-cloud-gateway-example
 * @date 17/4/24
 */
public class RunTimeAgent {
    public static void premain(String arg, Instrumentation instrumentation) {
        System.out.println("探针启动！！！");
        System.out.println("探针传入参数：" + arg);
        instrumentation.addTransformer(new RunTimeTransformer());
    }
}
