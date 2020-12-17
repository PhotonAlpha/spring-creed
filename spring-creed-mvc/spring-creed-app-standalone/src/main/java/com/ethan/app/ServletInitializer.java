package com.ethan.app;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * An opinionated {@link org.springframework.web.WebApplicationInitializer} to run a {@link org.springframework.boot.SpringApplication}
 *  * from a traditional WAR deployment. Binds {@link javax.servlet.Servlet}, {@link java.util.logging.Filter} and
 *  * {@link org.springframework.boot.web.servlet.ServletContextInitializer} beans from the application context to the server.
 */
public class ServletInitializer extends SpringBootServletInitializer {
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(CreedApplication.class);
  }
}
