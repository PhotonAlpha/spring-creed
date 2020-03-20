package com.ethan.auth.annotation;

import com.ethan.auth.authentication.authorization.OAuth2AuthorizationServer;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({OAuth2AuthorizationServer.class})
public @interface CreedAuthorizationServer {
}
