package com.ethan.app.config;

import com.ethan.auth.annotation.CreedAuthorizationServer;
import com.ethan.auth.annotation.CreedResourceServer;
import org.springframework.context.annotation.Configuration;

@Configuration
@CreedAuthorizationServer
@CreedResourceServer
public class CreedAuthorizationServerConfig {
}
