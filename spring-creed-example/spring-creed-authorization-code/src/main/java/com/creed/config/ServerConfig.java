package com.creed.config;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 设置服务器初始化时的IP值
 */
@Component
public class ServerConfig implements ApplicationListener<WebServerInitializedEvent> {
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(ServerConfig.class);
	@Value("${server.host}")
  private String ip;

  private int serverPort;

  public String getUrl() {
    InetAddress address = null;
    try {
      address = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }

    log.info("server start up with HostName:{} HostAddress:{} CanonicalHostName:{}", address.getHostName(), address.getHostAddress(), address.getCanonicalHostName());
    return "http://" + ip + ":" + this.serverPort;
  }

  @Override
  public void onApplicationEvent(WebServerInitializedEvent event) {
    String nameSpace = event.getApplicationContext().getServerNamespace();
    log.info("server start up with nameSpace:{}", nameSpace);
    this.serverPort = event.getWebServer().getPort();
  }
}
