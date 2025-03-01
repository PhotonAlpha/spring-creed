package com.ethan.agent.factory;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * @author EthanCao
 * @description spring-creed-agent
 * @date 19/11/24
 */
public abstract class AbstractDevBuddyTransformer<T> implements AgentBuilder.Transformer {
    private final ElementMatcher.Junction<T> matcher;

    protected AbstractDevBuddyTransformer(ElementMatcher.Junction<T> matcher) {
        // ElementMatcher主要用于赛选
        this.matcher = matcher;
    }

    public ElementMatcher.Junction<T> getMatcher() {
        return matcher;
    }
}
