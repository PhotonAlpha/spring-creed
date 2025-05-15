package com.ethan.agent;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.Map;
import java.util.Objects;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 17/4/24
 */
@Slf4j
public class RunTimeAgent {
    public static ResettableClassFileTransformer resettableClassFileTransformer;
    public static Instrumentation previousInstrumentation;
    public static void agentmain(String agentArgs, Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {
        System.out.println("""
                :: Power By ::
                 ▄▄▄▄▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄▄▄▄▄ ▄▄▄▄▄▄▄▄▄▄        ▄▄▄▄▄▄▄▄▄▄  ▄         ▄ ▄▄▄▄▄▄▄▄▄▄  ▄▄▄▄▄▄▄▄▄▄  ▄         ▄\s
                ▐░░░░░░░░░░░▐░░░░░░░░░░░▐░░░░░░░░░░░▐░░░░░░░░░░░▐░░░░░░░░░░▌      ▐░░░░░░░░░░▌▐░▌       ▐░▐░░░░░░░░░░▌▐░░░░░░░░░░▌▐░▌       ▐░▌
                ▐░█▀▀▀▀▀▀▀▀▀▐░█▀▀▀▀▀▀▀█░▐░█▀▀▀▀▀▀▀▀▀▐░█▀▀▀▀▀▀▀▀▀▐░█▀▀▀▀▀▀▀█░▌     ▐░█▀▀▀▀▀▀▀█░▐░▌       ▐░▐░█▀▀▀▀▀▀▀█░▐░█▀▀▀▀▀▀▀█░▐░▌       ▐░▌
                ▐░▌         ▐░▌       ▐░▐░▌         ▐░▌         ▐░▌       ▐░▌     ▐░▌       ▐░▐░▌       ▐░▐░▌       ▐░▐░▌       ▐░▐░▌       ▐░▌
                ▐░▌         ▐░█▄▄▄▄▄▄▄█░▐░█▄▄▄▄▄▄▄▄▄▐░█▄▄▄▄▄▄▄▄▄▐░▌       ▐░▌     ▐░█▄▄▄▄▄▄▄█░▐░▌       ▐░▐░▌       ▐░▐░▌       ▐░▐░█▄▄▄▄▄▄▄█░▌
                ▐░▌         ▐░░░░░░░░░░░▐░░░░░░░░░░░▐░░░░░░░░░░░▐░▌       ▐░▌     ▐░░░░░░░░░░▌▐░▌       ▐░▐░▌       ▐░▐░▌       ▐░▐░░░░░░░░░░░▌
                ▐░▌         ▐░█▀▀▀▀█░█▀▀▐░█▀▀▀▀▀▀▀▀▀▐░█▀▀▀▀▀▀▀▀▀▐░▌       ▐░▌     ▐░█▀▀▀▀▀▀▀█░▐░▌       ▐░▐░▌       ▐░▐░▌       ▐░▌▀▀▀▀█░█▀▀▀▀\s
                ▐░▌         ▐░▌     ▐░▌ ▐░▌         ▐░▌         ▐░▌       ▐░▌     ▐░▌       ▐░▐░▌       ▐░▐░▌       ▐░▐░▌       ▐░▌    ▐░▌    \s
                ▐░█▄▄▄▄▄▄▄▄▄▐░▌      ▐░▌▐░█▄▄▄▄▄▄▄▄▄▐░█▄▄▄▄▄▄▄▄▄▐░█▄▄▄▄▄▄▄█░▌     ▐░█▄▄▄▄▄▄▄█░▐░█▄▄▄▄▄▄▄█░▐░█▄▄▄▄▄▄▄█░▐░█▄▄▄▄▄▄▄█░▌    ▐░▌    \s
                ▐░░░░░░░░░░░▐░▌       ▐░▐░░░░░░░░░░░▐░░░░░░░░░░░▐░░░░░░░░░░▌      ▐░░░░░░░░░░▌▐░░░░░░░░░░░▐░░░░░░░░░░▌▐░░░░░░░░░░▌     ▐░▌    \s
                 ▀▀▀▀▀▀▀▀▀▀▀ ▀         ▀ ▀▀▀▀▀▀▀▀▀▀▀ ▀▀▀▀▀▀▀▀▀▀▀ ▀▀▀▀▀▀▀▀▀▀        ▀▀▀▀▀▀▀▀▀▀  ▀▀▀▀▀▀▀▀▀▀▀ ▀▀▀▀▀▀▀▀▀▀  ▀▀▀▀▀▀▀▀▀▀       ▀     \s
                (v1.0.0) For JDK21 RunTime
                """);
        System.setProperty("spring.main.allow-bean-definition-overriding", "true");
        System.setProperty("logging.config", "");
        System.setProperty("logging.dir", "./logs");
        System.setProperty("logging.file.name", "./logs/${logging.instance:${spring.application.name:GEBNGCUSG01}}.log");
        System.setProperty("logging.pattern.console", "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}) %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n");
        System.setProperty("logging.logback.rollingpolicy.file-name-pattern", "${LOG_FILE}.%d{yyyy-MM-dd}.%i.log");
        System.setProperty("logging.logback.rollingpolicy.max-file-size", "30MB");

        // ref:https://github.com/raphw/byte-buddy/issues/1164
        // https://stackoverflow.com/questions/72078883/class-retransformation-with-bytebuddy-agent
        // > Normally, when retransforming, the Advice API is better suited for transformation. It supports most features of the delegation API but works slightly different.
        log.info("@.@[agentArgs{}]@.@", agentArgs);
        log.info("@.@[::resettableClassFileTransformer::{}]@.@", Objects.nonNull(resettableClassFileTransformer));
        log.info("@.@[previousInstrumentation:{}]@.@", previousInstrumentation);
        if (Objects.nonNull(resettableClassFileTransformer)) {
            log.info("@.@[resetting......{}]@.@", resettableClassFileTransformer.toString());
            resettableClassFileTransformer.reset(previousInstrumentation, AgentBuilder.RedefinitionStrategy.RETRANSFORMATION);
        }
        if ("exit".equals(agentArgs)) {
            log.info("@.@[exit......{}]@.@", resettableClassFileTransformer.toString());
            resettableClassFileTransformer = null;
            previousInstrumentation = null;
            return;
        }
        // RedefinitionStrategy.REDEFINITION 修改现有的内容
        // RedefinitionStrategy.RETRANSFORMATION 可以动态增强类的定义或者添加新的字段或方法
        // ClassFileLocator locator = ClassFileLocator.ForFolder.of(new File("./classes"), "");
        // ClassInjector.UsingInstrumentation.of(locator, ClassInjector.UsingInstrumentation.Target.BOOTSTRAP, inst)
        //         .inject(locator.locate("").resolve());

        resettableClassFileTransformer = new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .disableClassFormatChanges()
                .with(new FlowListener())
                // .type(ElementMatchers.isAnnotatedWith(RestController.class))
                // .transform((builder, typeDescription,  classLoader,  module,  protectionDomain) -> {
                //     System.out.println("typeDescription with RestController:" + typeDescription);
                //     return builder
                //             .visit(Advice.to(MonitorRequestResponseInterceptor.class).on(ElementMatchers.isMethod()));
                // })
                .type(ElementMatchers.named("org.example.controller.TestController"))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> {
                    System.out.println("typeDescription:" + typeDescription);
                    return builder
                            .visit(Advice.to(TestControllerInterceptor.class).on(ElementMatchers.named("testAgent")));
                })
                .installOn(inst);


        log.info("@.@[>>>>>>>>>>>>>>>>>>>>> INITIALIZED AGENT{}]@.@", resettableClassFileTransformer.toString());
        previousInstrumentation = inst;
        log.info("@.@[current Instrumentation::{}]@.@" , inst);
    }


    public static class MonitorRequestResponseInterceptor {
        public static final Logger log = LoggerFactory.getLogger(MonitorRequestResponseInterceptor.class);
        public static final StopWatch STOP_WATCH = new StopWatch("MonitorRequestResponseInterceptor");
        @Advice.OnMethodEnter
        public static void enter(@Advice.This Object controller, @Advice.Origin String methodName, @Advice.AllArguments Object[] allArguments) {
            log.info("@.@[::Monitor OnMethodEnter Name:{} allArguments:{}]@.@", methodName, allArguments);
            STOP_WATCH.start();
        }
        @Advice.OnMethodExit // 在方法返回时执行
        public static void exit(@Advice.Origin String methodName, @Advice.Return Object returned) {
            log.info("@.@[::Monitor OnMethodExit Name:{} returned:{}]@.@", methodName, returned);
            STOP_WATCH.stop();
            log.info("@.@[methodName:{} cost:{}]@.@", methodName, STOP_WATCH.getTotalTimeMillis());
        }
    }

    public static class FlowListener implements AgentBuilder.Listener {
        @Override
        public void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeName.startsWith("org.example")) {
                log.info("@.@[onDiscovery:{} {} {} {}]@.@", typeName, classLoader, module, loaded);
            }
        }

        @Override
        public void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
            if (typeDescription.getName().contains("org.example")) {
                log.info("@.@[onTransformation:{}-{}-{}-{}-{}]@.@", typeDescription.getName(), classLoader, module, loaded, dynamicType);
                /* try {
                    dynamicType.saveIn(new File("/Users/Documents/bytebuddy/%s".formatted(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd-HH-mm-ss")))));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } */
                Map<TypeDescription, byte[]> allTypes = dynamicType.getAllTypes();
                for (Map.Entry<TypeDescription, byte[]> entry : allTypes.entrySet()) {
                    log.info("@.@[key:{}-{}-{} ]@.@", entry.getKey().getName(), entry.getKey().getCanonicalName(), entry.getKey().getClassFileVersion());
                }
            }
        }

        @Override
        public void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeDescription.getName().contains("org.example")) {
                log.info("@.@[onIgnored:{}]@.@", typeDescription.getName());
            }
        }

        @Override
        public void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
            log.info("@.@[onError:{}]@.@", typeName, throwable);
        }

        @Override
        public void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
            if (typeName.startsWith("org.example")) {
                log.info("@.@[onComplete:{}]@.@", typeName);
            }
        }
    }
}
