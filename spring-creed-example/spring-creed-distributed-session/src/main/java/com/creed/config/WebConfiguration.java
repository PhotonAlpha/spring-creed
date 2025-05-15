package com.creed.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;


@Component
public class WebConfiguration implements WebApplicationInitializer {
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    //我们可以使用httpOnly 和 secure 来保护cookie：
    //httpOnly： 如果设置为true，浏览器叫门将不能访问cookie；
    //secure: 如果设置为true，cookie将会使用https从新；
    servletContext.getSessionCookieConfig().setHttpOnly(true);
    servletContext.getSessionCookieConfig().setSecure(true);
  }
}
