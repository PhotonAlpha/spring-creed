package com.ethan.test.config;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.ServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientIpResolver implements HandlerMethodArgumentResolver {
  private static final String[] IP_HEADER_CANDIDATES = {
      "X-Forwarded-For",
      "Proxy-Client-IP",
      "WL-Proxy-Client-IP",
      "HTTP_X_FORWARDED_FOR",
      "HTTP_X_FORWARDED",
      "HTTP_X_CLUSTER_CLIENT_IP",
      "HTTP_CLIENT_IP",
      "HTTP_FORWARDED_FOR",
      "HTTP_FORWARDED",
      "HTTP_VIA",
      "REMOTE_ADDR"
  };

  @Override
  public boolean supportsParameter(MethodParameter methodParameter) {
    return methodParameter.getParameterType().equals(String.class) &&
        methodParameter.hasParameterAnnotation(ClientIp.class);
  }

  @Override
  public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
    // 提取header得到IP地址列表（多重代理场景），取第一个IP
    for (String header : IP_HEADER_CANDIDATES) {
      String ipList = nativeWebRequest.getHeader(header);
      if (ipList != null && ipList.length() != 0 &&
          !"unknown".equalsIgnoreCase(ipList)) {

        if (ipList != null && ipList.length() > 15) { // "***.***.***.***".length()
          // = 15
          if (ipList.indexOf(",") > 0) {
            ipList = ipList.substring(0, ipList.indexOf(","));
          }
        }

        return ipList.split(",")[0];
      }
    }

    // 没有经过代理或者SLB，直接 getRemoteAddr 方法获取IP
    String ip = ((ServletRequest) nativeWebRequest.getNativeRequest()).getRemoteAddr();

    // 如果是本地环回IP，则根据网卡取本机配置的IP
    if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
      try {
        InetAddress inetAddress = InetAddress.getLocalHost();
        return inetAddress.getHostAddress();
      } catch (UnknownHostException e) {
        e.printStackTrace();
        return ip;
      }
    }
    return ip;
  }
}
