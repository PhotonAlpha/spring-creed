/**
 * describe: copy right by @author
 *
 * @author xxx
 * @date 2019/12/10
 */
package com.ethan.context.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContextUtils implements ApplicationContextAware {
  private static ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  /**
   * Return the spring context
   */
  public static ApplicationContext getApplicationContext() {
    checkApplicationContext();
    return applicationContext;
  }

  /**
   * Return an instance, which may be shared or independent, of the specified bean.
   */
  public static <T> T getBean(String name) {
    checkApplicationContext();
    return (T) applicationContext.getBean(name);
  }

  /**
   * Return the bean instance that uniquely matches the given object type, if any.
   */
  public static <T> T getBean(Class<T> clazz) {
    checkApplicationContext();
    return applicationContext.getBean(clazz);
  }

  /**
   * remove applicationContext.
   */
  public static void cleanApplicationContext() {
    applicationContext = null;
  }

  private static void checkApplicationContext() {
    if (applicationContext == null) {
      throw new IllegalStateException("applicationContext not autowired, please ");
    }
  }
}
