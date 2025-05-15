package com.ethan.agent;

import com.ethan.agent.asm.PropertyResourceTransformerEnhance;
import com.ethan.agent.asm.PropertyValueTransformerEnhance;
import com.ethan.agent.factory.TransformFactory;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.Objects;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 17/4/24
 */
@Slf4j
public class PreMainAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
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
                (v1.0.0) For JDK21
                """);
        System.setProperty("spring.main.allow-bean-definition-overriding", "true");
        System.setProperty("logging.config", "");
        System.setProperty("logging.dir", "./logs");
        System.setProperty("logging.file.name", "./logs/${logging.instance:${spring.application.name:GEBNGCUSG01}}.log");
        System.setProperty("logging.pattern.console", "%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}) %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){magenta} %clr(---){faint} %clr([%15.15t]){faint} %clr(%-40.40logger{39}){cyan} %clr(:){faint} %m%n");
        System.setProperty("logging.logback.rollingpolicy.file-name-pattern", "${LOG_FILE}.%d{yyyy-MM-dd}.%i.log");
        System.setProperty("logging.logback.rollingpolicy.max-file-size", "30MB");
        // -- ASM增强
        inst.addTransformer(new PropertyResourceTransformerEnhance());
        inst.addTransformer(new PropertyValueTransformerEnhance());
        // --byte buddy增强

        var defaultBuilder = new AgentBuilder.Default();
        TransformFactory.init();
        var register = TransformFactory.getRegister();
        AgentBuilder.Identified.Extendable agentBuilder = null;
        for (Map.Entry<ElementMatcher, AgentBuilder.Transformer> entry : register.entrySet()) {
            // log.info("@.@[register matcher:{}]@.@", entry.getKey());
            if (Objects.isNull(agentBuilder)) {
                agentBuilder = defaultBuilder.type(entry.getKey())
                        .transform(entry.getValue());
            } else {
                agentBuilder = agentBuilder.type(entry.getKey())
                        .transform(entry.getValue());
            }
        }
        agentBuilder.installOn(inst);
    }
}
