package com.ethan.provider;

import com.ethan.HelloService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Value;

@Service(version = "${demo.service.version}")
public class HelloServiceImpl implements HelloService {
  /**
   * The default value of ${dubbo.application.name} is ${spring.application.name}
   */
  @Value("${dubbo.application.name}")
  private String serviceName;

  @Override
  public String sayHello(String name) {
    return String.format("[%s] : Hello, %s", serviceName, name);
  }
}
